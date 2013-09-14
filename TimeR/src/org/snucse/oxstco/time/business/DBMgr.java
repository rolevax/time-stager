package org.snucse.oxstco.time.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
  
public class DBMgr {  
    private DBHelper helper;  
    private SQLiteDatabase db;  
    
    static DBMgr theDBMgr = null;
    
    public static DBMgr getInstance(Context context) {
    	if (theDBMgr == null) {
    		theDBMgr = new DBMgr(context);
    	}
    	return theDBMgr ;
    }
      
    private DBMgr(Context context) {  
        helper = new DBHelper(context);  
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        db = helper.getWritableDatabase();  
    }  
      
    
    public void add(Time time) {
    	ContentValues cv = new ContentValues();  
        cv.put("type", time.type);  
        cv.put("subject", time.subject);  
        cv.put("datetime", time.datetime.toString());  
        db.insert("time", null, cv);
    }
    
    
    public void update(Time time) {  
        ContentValues cv = new ContentValues();  
        cv.put("type", time.type);  
        cv.put("subject", time.subject);  
        cv.put("datetime", time.datetime.toString());  
        db.update("time", cv, "_id = ?", new String[]{""+time.id});  
    }  
      
    public void delete(Time time) {  
        db.delete("time", "_id = ?", new String[]{""+time.id});  
    }  
      
    public List<Time> getListByType(int type) {
    	List<Time> times = new ArrayList<Time>();
    	Cursor c = queryByType(type) ;
    	while (c.moveToNext()) {
    		Time t = new Time();
    		t.id = c.getInt(c.getColumnIndex("_id"));
    		t.subject = c.getString(c.getColumnIndex("subject"));
    		t.type = c.getInt(c.getColumnIndex("type"));
    		t.datetime = Timestamp.valueOf(c.getString(c.getColumnIndex("datetime")));
    		times.add(t);
    	}
    	return times ;
    }
    
    public Cursor queryByType(int type) {
    	Cursor c = db.query("time", null, "type=?", new String[]{""+type}, null, null, null);
    	return c ;
    }
      
    public void closeDB() {  
        db.close();  
    }  
}  