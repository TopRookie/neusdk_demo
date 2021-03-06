package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryResult;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.rrpc.QResult;
import com.neucore.neulink.rrpc.TLibQueryCmd;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neusdk_demo.db.LibManagerService;
import com.neucore.neusdk_demo.db.UserDaoUtils;
import com.neucore.neusdk_demo.db.bean.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SampleFaceQueryListener implements ICmdListener<QueryResult> {
    private LibManagerService libManagerService;
    private UserDaoUtils userDaoUtils = null;
    private String TAG = "SampleFaceQueryListener";
    public SampleFaceQueryListener(){
        this.libManagerService = new LibManagerService(ContextHolder.getInstance().getContext());
    }
    @Override
    public QueryResult doAction(NeulinkEvent event) {

        NeulinkTopicParser.Topic topic = null;
        TLibQueryCmd cmd = (TLibQueryCmd)event.getSource();

        long count = userDaoUtils.count(cmd.getConds());

        long page = count/200;
        long mod = count%200;
        if(mod>0){
            page = page +1;
        }

        List<String> urls = new ArrayList<String>();
        List<String> md5s = new ArrayList<String>();
        QResult result = new QResult();
        if(page>0){
            for(int i=1;i<page+1;i++){
                List<User> dataList = userDaoUtils.query(cmd.getConds(),i-1);
                User[] dataArray = new User[dataList.size()];
                dataList.toArray(dataArray);
                File localFile = null;
                try {
                    /**
                     * 上传到存储服务可以根据
                     */
                    localFile = store(topic, "users", i, dataArray);
                }
                catch (Exception ex){}
                String md5 = null;
                try {
                    md5 = MD5Utils.getInstance().getMD5File(localFile.getAbsolutePath());
                }
                catch (Exception ex){}
                String url = StorageFactory.getInstance().uploadQData(localFile.getAbsolutePath(), RequestContext.getId(),i);
                md5s.add(md5);
                urls.add(url);
                localFile.delete();
            }
            result.setCount(count);
            result.setPage(page);
            result.setOffset(1);
            result.setUrl(urls.get(0));
            result.setMd5(md5s.get(0));
        }

        return new QueryResult();
    }

    protected File store(NeulinkTopicParser.Topic topic, String dataPath, int index, Object[] dataArray) throws IOException {
        String path = ContextHolder.getInstance().getContext().getFilesDir() + "/" + dataPath + "/" + topic.getReqId() + "/";
        new File(path).mkdirs();
        path = path + "/" + index + ".json";
        File localFile = new File(path);
        localFile.createNewFile();
        FileWriter fileWriter = new FileWriter(localFile);
        String logs = JSonUtils.toJson(dataArray);
        fileWriter.write(logs);
        fileWriter.close();
        return localFile;
    }
}
