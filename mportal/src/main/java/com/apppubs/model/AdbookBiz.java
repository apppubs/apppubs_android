package com.apppubs.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.apppubs.AppContext;
import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.bean.TUserDeptLink;
import com.apppubs.bean.http.AdbookInfoResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.Constants;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.Des3;
import com.apppubs.util.FileUtils;
import com.apppubs.util.LogM;
import com.apppubs.util.StringUtils;
import com.orm.SugarRecord;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdbookBiz extends BaseBiz {

    private final String FILE_NAME_ADBOOK_INFO = "adbook_info.cfg";
    private static volatile AdbookBiz sAdbookBiz;

    private AdbookBiz(Context context) {
        super(context);
    }

    public static AdbookBiz getInstance(Context context){
        if (sAdbookBiz == null){
            synchronized (AdbookBiz.class){
                if (sAdbookBiz == null){
                    sAdbookBiz = new AdbookBiz(context);
                }
            }
        }
        return sAdbookBiz;
    }

    public void fetchAdbookInfo(final IAPCallback<AdbookInfoResult> callback) {
        asyncPOST(Constants.API_NAME_ADBOOK_INFO, null, true, AdbookInfoResult.class, new
                IRQListener<AdbookInfoResult>() {
                    @Override
                    public void onResponse(final AdbookInfoResult jr, final APError error) {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                if (error == null) {
                                    callback.onDone(jr);
                                } else {
                                    callback.onException(error);
                                }
                            }
                        });

                    }
                });
    }

    public void cacheAdbookInfo(AdbookInfoResult result) {
        FileUtils.writeObj(mContext, result, FILE_NAME_ADBOOK_INFO);
    }

    public AdbookInfoResult getCachedAdbookInfo() {
        return (AdbookInfoResult) FileUtils.readObj(mContext, FILE_NAME_ADBOOK_INFO);
    }

    public List<TUser> getUsersByUserIds(List<String> userIds){
        StringBuilder sb = new StringBuilder();
        for (String userId : userIds){
            if (sb.length()>0){
                sb.append(",");
            }
            sb.append("'"+userId+"'");
        }
        String sql = "select * from USER where USER_ID in ("+sb.toString()+")";
        return SugarRecord.findWithQuery(TUser.class,sql);
    }

    public long countUserOfCertainDepartment(String deptId){
        int count = 0;

        List<TUser> user = new ArrayList<TUser>();
        List<String> deptIds = new ArrayList<String>();
        deptIds.add(deptId);
        recurseGet(deptId,deptIds);

        StringBuilder sb = new StringBuilder();
        for (String id:deptIds){
            if (sb.length()>0){
                sb.append(",");
            }
            sb.append("'");
            sb.append(id);
            sb.append("'");
        }
        String sql = String.format("select count(user_id) as usercount from user_dept_link where dept_id in(%s)",sb.toString());

        Cursor cursor = SugarRecord.getDatabase().rawQuery(sql,null);
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }


    public String getDepartmentStringByUserId(String userId){
        List<String> deptNameStringList = getDepartmentStringListByUserId(userId);
        StringBuilder sb = new StringBuilder();
        int size = deptNameStringList.size();
        for (int i = -1; ++i < size;) {
            if (i > 0) {
                sb.append("\n" + deptNameStringList.get(i));
            } else {
                sb.append(deptNameStringList.get(i));
            }

        }
        return sb.toString();
    }

    public List<String> getDepartmentStringListByUserId(String userId){
        List<TDepartment> deptList = getDepartmentByUserId(userId);
        List<String> strList = new ArrayList<String>(deptList.size());
        for(TDepartment dept:deptList){
            strList.add(getDepartmentStringByDeptId(dept.getDeptId()));
        }
        return strList;
    }

    /**
     * 获取除顶级部门外两级部门名称
     */
    private String getDepartmentStringByDeptId(String deptId){
        TDepartment dept = SugarRecord.findByProperty(TDepartment.class, "dept_id", deptId);
        AdbookInfoResult adbookInfo = getCachedAdbookInfo();
        StringBuilder sb = new StringBuilder();
        if(!deptId.equals(adbookInfo.getRootDeptId())){
            if(TextUtils.isEmpty(sb.toString())){
                sb.append(dept.getName());
            }else{
                sb.insert(0, dept.getName()+"-");
            }
            if (!TextUtils.isEmpty(dept.getSuperId())&&!dept.getSuperId().equals(adbookInfo.getRootDeptId())){
                TDepartment superDept = SugarRecord.findByProperty(TDepartment.class, "dept_id", dept.getSuperId());
                sb.insert(0,superDept.getName()+"-");
            }
        }
        return sb.toString();
    }

    public TDepartment getDepartmentById(String deptId) {
        List<TDepartment> deptList = SugarRecord.find(TDepartment.class, "dept_id=?", deptId);
        if (deptList == null || deptList.size() < 1) {
            return null;
        } else {
            return deptList.get(0);
        }
    }

    public List<TDepartment> getDepartmentByUserId(String userId) {
        String sql = "select * from DEPARTMENT t1 join USER_DEPT_LINK t2 on t1.DEPT_ID = t2.DEPT_ID where t2.USER_ID = ?";
        return SugarRecord.findWithQuery(TDepartment.class, sql, userId);
    }

    /**
     * 获取某一个user
     *
     * @param userId
     * @return
     */
    public TUser getUserByUserId(String userId) {
        return SugarRecord.findByProperty(TUser.class, "USER_ID", userId);
    }

    public List<TDepartment> listSubDepartments(String superDepId) {
        AdbookInfoResult info = getCachedAdbookInfo();
        if (info.needReadPermission()){
            return listSubDepartments(superDepId,info.getReadPermissionStr());
        }else{
            return listSubDepartments(superDepId, null);
        }
    }

    public List<TDepartment> listSubDepartments(String superDepId, String permissionString) {
        List<TDepartment> result = null;
        if (permissionString != null) {
            String sb = resovePermissionString(permissionString);
            String sql = "select * from department where super_id = '" + superDepId + "' and dept_id in (" + sb + ") " +
                    "order by sort_id";
            result = SugarRecord.findWithQuery(TDepartment.class, sql, new String[]{});
        } else {
            result = SugarRecord.find(TDepartment.class, "super_id = ?", new String[]{superDepId}, null, "SORT_ID",
                    null);
        }
        return result;
    }

    private String resovePermissionString(String permissionString) {
        String[] permissionArr = permissionString.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = -1; ++i < permissionArr.length; ) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append("'" + permissionArr[i] + "'");
        }
        return sb.toString();
    }

    /**
     * 判断某个department是否为叶子节点
     *
     * @param departmentId
     * @return
     */
    public boolean isLeaf(String departmentId) {
        long count = SugarRecord.count(TDepartment.class, "SUPER_ID = ?", new String[]{departmentId});
        return count == 0;
    }


    /**
     * 列出某个department下的用户
     * 当前系统需要限制通讯录权限且有部门权限则显示全部信息，否则只查询出userid和truename
     *
     * @param departmentId
     * @return
     */
    public List<TUser> listUser(String departmentId) {
        String sql = "select * from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? " +
                "order by t2.sort_id";

        return SugarRecord.findWithQuery(TUser.class, sql, departmentId);
    }

    /**
     * 某部门下的所有用户，包含子部门的用户
     * @param deptId
     * @return
     */
    public List<String> getUserIdsOfCertainDepartment(String deptId){
        return getUserIdsOfCertainDepartment(deptId,false);
    }

    public List<String> getUserIdsOfCertainDepartment(String deptId,boolean needChatPermission){

        List<String> userIdList = new ArrayList<String>();
        List<String> deptIds = new ArrayList<String>();
        deptIds.add(deptId);
        recurseGet(deptId,deptIds);

        StringBuilder sb = new StringBuilder();
        if (needChatPermission){
            String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getChatPermissionString();
            for (String id:deptIds){
                if (!TextUtils.isEmpty(permissionStr)&&permissionStr.contains(id)){
                    if (sb.length()>0){
                        sb.append(",");
                    }
                    sb.append("'");
                    sb.append(id);
                    sb.append("'");
                }
            }
        }else{
            for (String id:deptIds){
                if (sb.length()>0){
                    sb.append(",");
                }
                sb.append("'");
                sb.append(id);
                sb.append("'");
            }
        }

        String sql = String.format("select distinct user_id from user_dept_link where dept_id in(%s)",sb.toString());

        Cursor cursor = SugarRecord.getDatabase().rawQuery(sql,null);
        while (cursor.moveToNext()){
            String userid = cursor.getString(0);
            userIdList.add(userid);
        }
        cursor.close();
        return userIdList;
    }

    /**
     * 判断在是否有某用户的读取权限
     * @param userid
     * @return
     */
    public boolean hasReadPermissionOfUser(String userid){
        List<TDepartment> dl = getDepartmentByUserId(userid);
        for (TDepartment d: dl){
            if (hasReadPermissionOfDept(d.getDeptId())){
                return true;
            }
        }
        return false;
    }

    private boolean hasReadPermissionOfDept(String deptId){
        if (TextUtils.isEmpty(deptId)){
            return false;
        }
        String permissionStr = "";
        String [] deptIds = permissionStr.split(",");
        for (String curDeptId : deptIds){
            if (deptId.equals(curDeptId)){
                return true;
            }
        }
        return false;
    }

    public boolean hasChatPermissionOfUser(String userId){
        AdbookInfoResult result = getCachedAdbookInfo();
        if (result.needReadPermission()){
            List<TDepartment> dl = getDepartmentByUserId(userId);
            String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getChatPermissionString();
            for (TDepartment d: dl){
                if (!TextUtils.isEmpty(permissionStr)&&hasChatPermissionOfDept(d.getDeptId())){
                    return true;
                }
            }
        }
        return true;
    }

    private boolean hasChatPermissionOfDept(String deptId){
        if (TextUtils.isEmpty(deptId)){
            return false;
        }
        String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getChatPermissionString();
        String[] deptIds = permissionStr.split(",");
        for (String curDeptId : deptIds){
            if (curDeptId.equals(deptId)){
                return true;
            }
        }

        return false;
    }

    private void recurseGet(String deptId, List<String> deptIds) {
        List<TDepartment> depts = SugarRecord.find(TDepartment.class,"super_id=?",deptId);
        if (depts==null||depts.size()<1){
            return;
        }
        for (TDepartment dept:depts){
            deptIds.add(dept.getDeptId());
            recurseGet(dept.getDeptId(),deptIds);
        }
    }


    /**
     * 记录用户使用记录
     *
     * @param userId
     */
    public void recordUser(String userId) {
        SugarRecord.update(TUser.class, "LAST_USED_TIME", new Date().getTime() + "", "USER_ID = ?",
                new String[] { userId });
    }

    /*
     * 列出常用用户
     */
    public List<TUser> listRectent() {

        return SugarRecord.find(TUser.class, "LAST_USED_TIME IS NOT NULL", null, null, "LAST_USED_TIME desc", "0,20");
    }

    /**
     * 搜索用户
     *
     * @param str
     * @return
     */
    public List<TUser> searchUser(String str) {

        String dimStr = "%"+str+"%";
        return SugarRecord
                .find(TUser.class, "TRUE_NAME like ? or mobile like ? or work_tel like ? or office_no like ? or email like ?", new String[] { dimStr,dimStr,dimStr,dimStr,dimStr }, null, "sort_id", null);
    }

    public void parseXML(final File file, final IAPCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AdbookXMLParser parser = new AdbookXMLParser();
                try {
                    parser.parseXML(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(new APError(APErrorCode.GENERAL_ERROR, "xml解析错误！"));
                        }
                    });
                }
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDone(null);
                    }
                });
            }
        }).start();


    }

    private class AdbookXMLParser {
        private int mParsingObjectFlag;//当前解析对象的flag 1=user 2=dept 3=addept
        private Object mCurBeanObject;
        private String mCurPropertyName;
        private boolean needEncrypt;

        private void parseXML(File file) throws Exception {
            XmlPullParser parser = getParser(file);
            SQLiteDatabase db = SugarRecord.getDatabase();
            SugarRecord.deleteAll(TUser.class);
            SugarRecord.deleteAll(TDepartment.class);
            SugarRecord.deleteAll(TUserDeptLink.class);
            int eventType = parser.getEventType();
            while (true) {
                if (eventType == parser.START_DOCUMENT) {
                    LogM.log(this.getClass(), "start_document");
                } else if (eventType == parser.END_DOCUMENT) {
                    LogM.log(this.getClass(), "end_document");
                    break;//此处跳出循环
                } else if (eventType == parser.START_TAG) {
                    mCurPropertyName = parser.getName();
                    if (("user").equals(parser.getName())) {
                        mParsingObjectFlag = 1;
                        mCurBeanObject = new TUser();
                    } else if ("dept".equals(parser.getName())) {
                        mParsingObjectFlag = 2;
                        mCurBeanObject = new TDepartment();
                    } else if ("link".equals(parser.getName())) {
                        mParsingObjectFlag = 3;
                        mCurBeanObject = new TUserDeptLink();
                    } else if ("adbook".equals(parser.getName())) {
                        needEncrypt = Boolean.parseBoolean(parser.getAttributeValue(0));
                    }
                } else if (eventType == parser.END_TAG) {
                    if ("user".equals(parser.getName())) {
                        TUser user = (TUser) mCurBeanObject;
                        TUser.executeQuery("insert into user (user_id,username,true_name,email,mobile,mobile2," +
                                        "work_tel,office_no) values(?,?,?,?,?,?,?,?)", user.getUserId(), user
                                        .getUsername(),
                                user.getTrueName(), user.getEmail(), user.getMobile(), user.getMobile2(), user
                                        .getWorkTEL(), user.getOfficeNO());
                    } else if ("dept".equals(parser.getName())) {
                        TDepartment dept = (TDepartment) mCurBeanObject;
                        TDepartment.executeQuery("insert into department (dept_id,name,level,super_id,sort_id," +
                                "total_num) values(?,?,?,?,?,?)", dept.getDeptId(), dept.getName(), dept.getLevel() +
                                "", dept.getSuperId(), dept.getSortId() + "", dept.getTotalNum() + "");
                    } else if ("link".equals(parser.getName())) {
                        TUserDeptLink link = (TUserDeptLink) mCurBeanObject;
                        TUserDeptLink.executeQuery("insert into user_dept_link (user_id,dept_id,sort_id) values" +
                                "(?,?,?)", link.getUserId(), link.getDepId(), link.getSortId() + "");
                    }
                    mCurPropertyName = null;
                } else if (eventType == parser.TEXT) {
                    if (mParsingObjectFlag == 1) {
                        TUser user = (TUser) mCurBeanObject;
                        if ("userid".equals(mCurPropertyName)) {
                            if (user.getUserId() == null) {
                                user.setUserId(parser.getText());
                            }
                        } else if ("username".equals(mCurPropertyName)) {
                            if (user.getUsername() == null) {
                                user.setUsername(parser.getText());
                            }
                        } else if ("truename".equals(mCurPropertyName)) {
                            if (user.getTrueName() == null) {
                                if (needEncrypt) {
                                    user.setTrueName(Des3.decode(parser.getText()));
                                } else {
                                    user.setTrueName(parser.getText());
                                }
                            }
                        } else if ("email".equals(mCurPropertyName)) {
                            if (user.getEmail() == null) {
                                if (needEncrypt) {
                                    user.setEmail(Des3.decode(parser.getText()));
                                } else {
                                    user.setEmail(parser.getText());
                                }
                            }
                        } else if ("mobile".equals(mCurPropertyName)) {
                            if (user.getMobile() == null) {
                                if (needEncrypt) {
                                    user.setMobile(Des3.decode(parser.getText()));
                                } else {
                                    user.setMobile(parser.getText());
                                }
                            }
                        } else if ("mobile2".equals(mCurPropertyName)) {
                            if (user.getMobile2() == null) {
                                if (needEncrypt) {
                                    user.setMobile2(Des3.decode(parser.getText()));
                                } else {
                                    user.setMobile2(parser.getText());
                                }
                            }
                        } else if ("worktel".equals(mCurPropertyName)) {
                            if (user.getWorkTEL() == null) {
                                if (needEncrypt) {
                                    user.setWorkTEL(Des3.decode(parser.getText()));
                                } else {
                                    user.setWorkTEL(parser.getText());
                                }
                            }
                        } else if ("officeno".equals(mCurPropertyName)) {
                            if (user.getOfficeNO() == null) {
                                if (needEncrypt) {
                                    user.setOfficeNO(Des3.decode(parser.getText()));
                                } else {
                                    user.setOfficeNO(parser.getText());
                                }
                            }
                        }
                    } else if (mParsingObjectFlag == 2) {
                        TDepartment dept = (TDepartment) mCurBeanObject;
                        if ("deptid".equals(mCurPropertyName)) {
                            if (dept.getDeptId() == null) {
                                dept.setDeptId(parser.getText());
                            }
                        } else if ("deptname".equals(mCurPropertyName)) {
                            if (dept.getName() == null) {
                                dept.setName(parser.getText());
                            }
                        } else if ("deptlevel".equals(mCurPropertyName)) {
                            if (dept.getLevel() == null) {
                                dept.setLevel(parser.getText());
                            }
                        } else if ("parentid".equals(mCurPropertyName)) {
                            if (dept.getSuperId() == null) {
                                dept.setSuperId(parser.getText());
                            }
                        } else if ("ordernum".equals(mCurPropertyName)) {
                            if (dept.getSortId() == 0) {
                                if (StringUtils.isInteger(parser.getText())) {
                                    dept.setSortId(Integer.parseInt(parser.getText()));
                                }
                            }
                        } else if ("totalusernum".equals(mCurPropertyName)) {
                            if (dept.getTotalNum() == 0) {
                                if (StringUtils.isInteger(parser.getText())) {
                                    dept.setTotalNum(Integer.parseInt(parser.getText()));
                                }
                            }
                        }
                    } else if (mParsingObjectFlag == 3) {
                        TUserDeptLink link = (TUserDeptLink) mCurBeanObject;
                        if ("userid".equals(mCurPropertyName)) {
                            if (link.getUserId() == null) {
                                link.setUserId(parser.getText());
                            }
                        } else if ("deptid".equals(mCurPropertyName)) {
                            if (link.getDepId() == null) {
                                link.setDeptId(parser.getText());
                            }
                        } else if ("deptid".equals(mCurPropertyName)) {
                            if (link.getSortId() == 0) {
                                if (StringUtils.isInteger(parser.getText())) {
                                    link.setSortId(Integer.parseInt(parser.getText()));
                                }
                            }
                        }
                    }
                }
                eventType = parser.next();
            }
        }

        private XmlPullParser getParser(File file) throws XmlPullParserException, FileNotFoundException {
            XmlPullParser parser = null;
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new FileReader(file));
            return parser;
        }
    }
}
