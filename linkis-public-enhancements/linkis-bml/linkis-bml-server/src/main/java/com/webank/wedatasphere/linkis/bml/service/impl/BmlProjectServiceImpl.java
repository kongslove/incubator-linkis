package com.webank.wedatasphere.linkis.bml.service.impl;

import com.webank.wedatasphere.linkis.bml.Entity.BmlProject;
import com.webank.wedatasphere.linkis.bml.dao.BmlProjectDao;
import com.webank.wedatasphere.linkis.bml.service.BmlProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BmlProjectServiceImpl implements BmlProjectService {


    private static final Logger LOGGER = LoggerFactory.getLogger(BmlProjectServiceImpl.class);

    public static final Integer DEFAULT_EDIT_PRIV = 7;
    public static final Integer DEFAULT_ACCESS_PRIV = 5;
    public static final Integer DEFAULT_ADMIN_PRIV = 8;

    @Autowired
    private BmlProjectDao bmlProjectDao;

    @Override
    public int createBmlProject(String projectName, String creator, List<String> editUsers, List<String> accessUsers) {
        BmlProject bmlProject1 = bmlProjectDao.getBmlProject(projectName);
        if (bmlProject1 != null){
            return bmlProject1.getId();
        }
        Date createTime = new Date(System.currentTimeMillis());
        BmlProject bmlProject = new BmlProject();
        bmlProject.setName(projectName);
        bmlProject.setCreator(creator);
        bmlProject.setCreateTime(createTime);
        bmlProject.setDescription(creator + " 在bml创建的工程 ");
        bmlProject.setEnabled(1);
        bmlProject.setSystem("dss");
        bmlProjectDao.createNewProject(bmlProject);
        //2 将用户和工程绑定
        if (!editUsers.contains(creator)){
            editUsers.add(creator);
        }
        if(editUsers.size() > 0) {
            setProjectEditPriv(bmlProject.getName(), editUsers);
        }
        if(accessUsers.size() > 0) {
            setProjectAccessPriv(bmlProject.getName(), accessUsers);
        }
        return bmlProject.getId();
    }

    @Override
    public boolean checkEditPriv(String projectName, String username) {
        try{
            Integer priv = bmlProjectDao.getPrivInProject(projectName, username);
            return priv >= DEFAULT_EDIT_PRIV;
        }catch(Exception e){
            return true;
        }
    }

    @Override
    public boolean checkAccessPriv(String projectName, String username) {
        try{
            Integer priv = bmlProjectDao.getPrivInProject(projectName, username);
            return priv >= DEFAULT_ACCESS_PRIV;
        }catch(Exception e){
            return true;
        }
    }

    @Override
    public void setProjectEditPriv(String projectName, List<String> editUsers) {
        BmlProject bmlProject = bmlProjectDao.getBmlProject(projectName);
        String creator = bmlProject.getCreator();
        Date createTime = new Date(System.currentTimeMillis());
        bmlProjectDao.setProjectPriv(bmlProject.getId(), editUsers, DEFAULT_EDIT_PRIV, creator, createTime);
    }

    @Override
    public void addProjectEditPriv(String projectName, List<String> editUsers) {

    }

    @Override
    public void deleteProjectEditPriv(String projectName, List<String> editUsers) {

    }

    @Override
    public void setProjectAccessPriv(String projectName, List<String> accessUsers) {
        BmlProject bmlProject = bmlProjectDao.getBmlProject(projectName);
        String creator = bmlProject.getCreator();
        Date createTime = new Date(System.currentTimeMillis());
        bmlProjectDao.setProjectPriv(bmlProject.getId(), accessUsers, DEFAULT_ACCESS_PRIV, creator, createTime);
    }

    @Override
    public void addProjectAccessPriv(String projectName, List<String> accessUsers) {

    }

    @Override
    public void deleteProjectAccessPriv(String projectName, List<String> accessUsers) {

    }

    @Override
    public String getProjectNameByResourceId(String resourceId) {
        return bmlProjectDao.getProjectNameByResourceId(resourceId);
    }

    @Override
    public void addProjectResource(String resourceId, String projectName) {
        BmlProject bmlProject = bmlProjectDao.getBmlProject(projectName);
        if (null != bmlProject) {
            bmlProjectDao.addProjectResource(bmlProject.getId(), resourceId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void attach(String projectName, String resourceId) {
        Integer projectId = bmlProjectDao.getProjectIdByName(projectName);
        Integer cnt = bmlProjectDao.checkIfExists(projectId, resourceId);
        if (cnt > 0) {
            return;
        }
        bmlProjectDao.attachResourceAndProject(projectId, resourceId);
    }

    @Override
    public void updateProjectUsers(String username, String projectName, List<String> editUsers, List<String> accessUsers) {
        Integer projectId = bmlProjectDao.getProjectIdByName(projectName);
        if (projectId == null){
            LOGGER.error("{} does not exist", projectName);
        }else{
            Date updateTime = new Date(System.currentTimeMillis());
            bmlProjectDao.deleteAllPriv(projectId);
            if (editUsers.size() > 0) {
                bmlProjectDao.setProjectPriv(projectId, editUsers, DEFAULT_EDIT_PRIV, username, updateTime);
            }
            if (accessUsers.size() > 0){
                bmlProjectDao.setProjectPriv(projectId, accessUsers, DEFAULT_ACCESS_PRIV, username, updateTime);
            }
        }
    }
}
