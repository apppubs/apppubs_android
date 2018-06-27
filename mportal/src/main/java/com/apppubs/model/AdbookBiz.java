package com.apppubs.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.bean.TUserDeptLink;
import com.apppubs.bean.http.AdbookInfoResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.Constants;
import com.apppubs.presenter.AdbookPresenter;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.FileUtils;
import com.apppubs.util.StringUtils;
import com.orm.SugarRecord;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AdbookBiz extends BaseBiz {

    private final String FILE_NAME_ADBOOK_INFO = "adbook_info.cfg";

    public AdbookBiz(Context context) {
        super(context);
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

    public void parseXML(final File file, final IAPCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AdbookXMLParser parser = new AdbookXMLParser();
                parser.parseXML(file);
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

        private void parseXML(File file) {
            try {
                XmlPullParser parser = getParser(file);
                parseXml(parser);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private XmlPullParser getParser(File file) throws XmlPullParserException, FileNotFoundException {
            XmlPullParser parser = null;
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new FileReader(file));
            return parser;
        }


        private void parseXml(XmlPullParser parser) throws XmlPullParserException, IOException {

            SQLiteDatabase db = SugarRecord.getDatabase();
            db.beginTransaction();
            SugarRecord.deleteAll(TUser.class);
            SugarRecord.deleteAll(TDepartment.class);
            SugarRecord.deleteAll(TUserDeptLink.class);

            int eventType = parser.getEventType();
            do {
                if (eventType == parser.START_DOCUMENT) {
                } else if (eventType == parser.END_DOCUMENT) {
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
                    }
                } else if (eventType == parser.END_TAG) {
                    if ("user".equals(parser.getName())) {
                        TUser user = (TUser) mCurBeanObject;
                        user.save();
                    } else if ("dept".equals(parser.getName())) {
                        TDepartment dept = (TDepartment) mCurBeanObject;
                        dept.save();
                    } else if ("link".equals(parser.getName())) {
                        TUserDeptLink link = (TUserDeptLink) mCurBeanObject;
                        link.save();
                    }
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
                                user.setTrueName(parser.getText());
                            }
                        } else if ("email".equals(mCurPropertyName)) {
                            if (user.getEmail() == null) {
                                user.setEmail(parser.getText());
                            }
                        } else if ("mobile".equals(mCurPropertyName)) {
                            if (user.getMobile() == null) {
                                user.setMobile(parser.getText());
                            }
                        } else if ("mobile2".equals(mCurPropertyName)) {
                            if (user.getMobile2() == null) {
                                user.setMobile2(parser.getText());
                            }
                        } else if ("worktel".equals(mCurPropertyName)) {
                            if (user.getWorkTEL() == null) {
                                user.setWorkTEL(parser.getText());
                            }
                        } else if ("officeno".equals(mCurPropertyName)) {
                            if (user.getOfficeNO() == null) {
                                user.setOfficeNO(parser.getText());
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
                        }else if("totalusernum".equals(mCurPropertyName)){
                            if (dept.getTotalNum() == 0){
                                if (StringUtils.isInteger(parser.getText())){
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
            } while (eventType != parser.END_DOCUMENT);
            db.setTransactionSuccessful();
            db.endTransaction();
        }

    }
}
