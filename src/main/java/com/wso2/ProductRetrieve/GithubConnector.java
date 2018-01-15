package com.wso2.ProductRetrieve;

import com.wso2.Constants;
import com.wso2.Model.Product;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;


import java.io.File;
import java.io.IOException;


public class GithubConnector {

    Git git;





    public boolean update(Product product){

        try {
            boolean Successful =Git.open(new File(product.getSubdirectory())).pull().call().isSuccessful();
            System.out.println(Successful);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e){
            e.printStackTrace();
        }



        return true;
    }

    public String clone(Product product){
        try {

            git = Git.cloneRepository().setURI(product.getUrl()).setDirectory(new File(product.getSubdirectory())).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constants.ROOT_PATH;
    }

    public boolean pullRequest(Product product){
        return true;
    }


}
