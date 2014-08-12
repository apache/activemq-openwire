/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.fusesource.scalate.RenderContext

package

/**
 * <p>
 * </p>
 */
object Website {

  val project_name= "ActiveMQ OpenWire"
  val project_slogan= "ActiveMQ's OpenWire Protocol library"
  val project_id= "OpenWire"
  val project_jira_key= "OPENWIRE"
  val project_issue_url= "https://issues.apache.org/jira/browse/OPENWIRE"
  val project_forums_url= "http://activemq.2283324.n4.nabble.com/ActiveMQ-Dev-f2368404.html"
  val project_wiki_url= "https://cwiki.apache.org/confluence/display/ACTIVEMQ/Index"
  val project_logo= "/images/project-logo.png"
  val project_version= "1.0"
  val project_snapshot_version= "1.0-SNAPSHOT"
  val project_versions = List(
        project_version,
        "1.0"
        )

  val project_keywords= "messaging,openwire,jms,activemq,apollo,protocol"

  // -------------------------------------------------------------------
  val github_page= "https://github.com/apache/activemq-openwire"
  val git_user_url= "git://github.com/apache/activemq-openwire"
  val git_commiter_url= "https://git-wip-us.apache.org/repos/asf/activemq-openwire.git"
  val git_branch= "master"
  val git_edit_page_base = github_page+"/edit/"+git_branch+"/website/src"
  val disqus_shortname = project_id

  // REMOVE ME -------------------------------------------------------------------
  val project_git_url= "https://git-wip-us.apache.org/repos/asf/activemq-openwire.git"
  val project_git_trunk_url= project_git_url +"/master"
  val project_git_branches_url= project_git_url + "/branches"
  val project_git_tags_url= project_git_url + "/tags"
  val project_git_commiter_url= project_git_trunk_url.replaceFirst("http","https")

  val project_maven_groupId= "org.apache.activemq"
  val project_maven_artifactId= "openwire-protocol"

  val website_base_url= "http://activemq.apache.org/openwire"
}