/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.wso2.DependencyProcessor;

import com.wso2.Constants;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * TODO:Class level comment
 */
public class POMWriter {
    public void writePom(Model updatedModel){
        MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();

        Writer fileWriter = null;
        try{
            File pomFile = new File(updatedModel.getProjectDirectory().getAbsolutePath()+File.separator+Constants.POM_NAME);
            fileWriter = WriterFactory.newXmlWriter(pomFile);
            mavenXpp3Writer.write(fileWriter, updatedModel);

        } catch (IOException e){
            e.printStackTrace();
        } finally{
            IOUtil.close(fileWriter);
        }
    }
}
