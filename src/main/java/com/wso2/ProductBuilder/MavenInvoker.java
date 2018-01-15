package com.wso2.ProductBuilder;

import com.wso2.Constants;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;

public class MavenInvoker {


    public static boolean mavenBuilder(){

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(Constants.ROOT_PATH+File.separator+"Message Broker"));
        request.setGoals( Collections.singletonList( "install" ) );
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File("/usr/share/maven"));

        InvocationResult invocationResult;
        try {
            invocationResult =invoker.execute(request);
            
            if(invocationResult.getExitCode()==0){
                return true;
            }
            else{
                return false;
            }

        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }

        return false;


    }
}
