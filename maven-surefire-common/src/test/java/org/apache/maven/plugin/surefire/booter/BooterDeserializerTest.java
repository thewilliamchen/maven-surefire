package org.apache.maven.plugin.surefire.booter;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.maven.surefire.booter.BooterConfiguration;
import org.apache.maven.surefire.booter.BooterDeserializer;
import org.apache.maven.surefire.booter.ClasspathConfiguration;
import org.apache.maven.surefire.booter.ForkConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Performs roundtrip testing of serialization/deserialization
 * @author Kristian Rosenvold
 */
public class BooterDeserializerTest
    extends TestCase
{

    public static ForkConfiguration getForkConfiguration()
        throws IOException
    {
        return new ForkConfiguration( true, false, ForkConfiguration.FORK_NEVER );
    }

    public void testDirectoryScannerParams()
        throws IOException
    {
        ForkConfiguration forkConfiguration = getForkConfiguration();
        BooterConfiguration booterConfiguration = getTestBooterConfiguration( forkConfiguration );

        File aDir = new File( "." );
        List includes = new ArrayList();
        includes.add( "abc" );
        includes.add( "cde" );
        List excludes = new ArrayList();
        excludes.add( "xx1" );
        excludes.add( "xx2" );

        BooterSerializer booterSerializer = new BooterSerializer();
        booterConfiguration.setDirectoryScannerOptions( aDir, includes, excludes );
        Properties props = new Properties();
        booterSerializer.setForkProperties( props, new ArrayList(), booterConfiguration, forkConfiguration );
        final File propsTest = booterSerializer.writePropertiesFile( "propsTest", props, false, null );

        BooterDeserializer booterDeserializer = new BooterDeserializer();
        BooterConfiguration read = booterDeserializer.deserialize( new FileInputStream( propsTest ) );

        Assert.assertEquals( aDir, read.getBaseDir() );
        Assert.assertEquals( includes.get( 0 ), read.getIncludes().get( 0 ) );
        Assert.assertEquals( includes.get( 1 ), read.getIncludes().get( 1 ) );
        Assert.assertEquals( excludes.get( 0 ), read.getExcludes().get( 0 ) );
        Assert.assertEquals( excludes.get( 1 ), read.getExcludes().get( 1 ) );

    }

    private BooterConfiguration getTestBooterConfiguration( ForkConfiguration forkConfiguration )
        throws IOException
    {
        ClasspathConfiguration classpathConfiguration = new ClasspathConfiguration( true, true );

        return new BooterConfiguration( forkConfiguration, classpathConfiguration, false );
    }
}