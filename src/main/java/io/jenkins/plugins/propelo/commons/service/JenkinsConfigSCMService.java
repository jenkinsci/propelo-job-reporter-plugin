package io.jenkins.plugins.propelo.commons.service;

import com.google.common.base.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JenkinsConfigSCMService {

    private static final Pattern DEPOT_PATH = Pattern.compile("//([^/]+)/", Pattern.CASE_INSENSITIVE);

    //region Helper
    private Optional<Element> findOne(Document currentNode, String tagName){
        NodeList resultNodes = currentNode.getElementsByTagName(tagName);
        if ((resultNodes == null) || (resultNodes.getLength() == 0)){
            return Optional.absent();
        }
        Node resultNode = resultNodes.item(0);
        if (resultNode.getNodeType() != Node.ELEMENT_NODE){
            return Optional.absent();
        }
        return Optional.fromNullable((Element)resultNode);
    }
    private Optional<Element> findOne(Element currentNode, String... tagNames){
        for(String tagName : tagNames){
            NodeList resultNodes = currentNode.getElementsByTagName(tagName);
            if ((resultNodes == null) || (resultNodes.getLength() == 0)){
                return Optional.absent();
            }
            Node resultNode = resultNodes.item(0);
            if (resultNode.getNodeType() != Node.ELEMENT_NODE){
                return Optional.absent();
            }
            currentNode = (Element) resultNode;
        }
        return Optional.fromNullable(currentNode);
    }
    private String sanitizeGitUrl(String gitUrl){
        if(gitUrl.endsWith("/")){
            gitUrl = gitUrl.substring(0, gitUrl.length()-1) + ".git";
        } else {
            gitUrl = gitUrl + ".git";
        }
        return gitUrl;
    }
    //endregion

    //GitSCM Plugin is used to configure both Git and Bitbucket Repo
    private Optional<SCMResult> parseGitScmPlugin(Document doc){
        Optional<Element> scm = findOne(doc, "scm");
        if(!scm.isPresent()){
            return Optional.absent();
        }
        Optional<Element> url = findOne(scm.get(), "userRemoteConfigs", "hudson.plugins.git.UserRemoteConfig", "url");
        if(!url.isPresent()){
            return Optional.absent();
        }
        String gitUrl = url.get().getTextContent();
        SCMResult result = new SCMResult(gitUrl, null);
        return Optional.fromNullable(result);
    }

    //Parse Bitbucket SCM Navigator - Bitbucket Url and Repo Username is specified
    private Optional<SCMResult> parseBitbucketSCMNavigator(Document doc){
        Optional<Element> navigators = findOne(doc, "navigators");
        if(!navigators.isPresent()){
            return Optional.absent();
        }
        Optional<Element> url = findOne(navigators.get(), "com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator", "serverUrl");
        if(!url.isPresent()){
            return Optional.absent();
        }
        String gitUrl = url.get().getTextContent();
        Optional<Element> userName = findOne(navigators.get(), "com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator", "repoOwner");
        if(!userName.isPresent()){
            return Optional.absent();
        }
        String gitUserName = userName.get().getTextContent();
        SCMResult result = new SCMResult(gitUrl, gitUserName);
        return Optional.fromNullable(result);
    }

    private Optional<SCMResult> parseGithubProjectProperty(Document doc){
        Optional<Element> properties = findOne(doc, "properties");
        if(!properties.isPresent()){
            return Optional.absent();
        }

        Optional<Element> url = findOne(properties.get(), "com.coravy.hudson.plugins.github.GithubProjectProperty", "projectUrl");
        if(!url.isPresent()){
            return Optional.absent();
        }

        String gitUrl = sanitizeGitUrl(url.get().getTextContent());
        SCMResult result = new SCMResult(gitUrl, null);
        return Optional.fromNullable(result);
    }

    private Optional<SCMResult> parseGitHubPushTrigger(Document doc){
        Optional<Element> definition = findOne(doc, "definition");
        if(!definition.isPresent()){
            return Optional.absent();
        }

        Optional<Element> url = findOne(definition.get(), "scm", "userRemoteConfigs", "hudson.plugins.git.UserRemoteConfig", "url");
        if(!url.isPresent()){
            return Optional.absent();
        }

        String gitUrl = sanitizeGitUrl(url.get().getTextContent());

        SCMResult result = new SCMResult(gitUrl, null);
        return Optional.fromNullable(result);
    }

    private Optional<SCMResult> parsePerforceScmPlugin(Document doc){
        Optional<Element> scm = findOne(doc, "scm");
        if(!scm.isPresent()){
            return Optional.absent();
        }
        Optional<Element> url = findOne(scm.get(), "workspace","streamName");
        if(!url.isPresent()){
            return Optional.absent();
        }
        String perforceStreamName = url.get().getTextContent();
        Matcher depotMatcher = DEPOT_PATH.matcher(perforceStreamName);
        String pathDepotName = null;
        if (depotMatcher.find()) {
            pathDepotName = depotMatcher.group(1);
        }
        SCMResult result = new SCMResult(pathDepotName, null);
        return Optional.fromNullable(result);
    }

    public Optional<SCMResult> parseSCMData(File xmlConfigFile) throws ParserConfigurationException, IOException, SAXException {
        if(!xmlConfigFile.exists()){
            return Optional.absent();
        }
        //an instance of factory that gives a document builder
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
        try {
            dbf.setFeature(FEATURE, true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("ParserConfigurationException was thrown. The feature '"
                    + FEATURE + "' is not supported by your XML processor.", e);
        }
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlConfigFile);
        doc.getDocumentElement().normalize();
        Optional<SCMResult> result = parseGitHubPushTrigger(doc);
        if(result.isPresent()){
            return result;
        }
        result = parseGitScmPlugin(doc);
        if(result.isPresent()){
            return result;
        }
        result = parseBitbucketSCMNavigator(doc);
        if(result.isPresent()){
            return result;
        }
        result = parseGithubProjectProperty(doc);
        if(result.isPresent()){
            return result;
        }
        result = parsePerforceScmPlugin(doc);
        if(result.isPresent()){
            return result;
        }

        return Optional.absent();
    }
    public static class SCMResult {
        private String url;
        private String userName;

        public SCMResult() {
        }

        public SCMResult(String url, String userName) {
            this.url = url;
            this.userName = userName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @Override
        public String toString() {
            return "SCMResult{" +
                    "url='" + url + '\'' +
                    ", userName='" + userName + '\'' +
                    '}';
        }
    }
}
