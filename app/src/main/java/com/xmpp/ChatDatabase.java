package com.xmpp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cync.model.ChatMessage;
import com.cync.model.ChatMessageNotification;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatDatabase {

	public static SQLiteDatabase DatabaseLite = null;
	private final static String DATABASE = "UserInfoDB";
	private static final int db_version = 1;
	Context con;
	DatabaseHelper dbhelp;
	static ChatDatabase instance;
	private final static String TABLE_CHAT_HISTORY = "ChatHistory";

	private static final String CREATE_CHAT_HISTORY = "create table ChatHistory(ID INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT, receiverid TEXT, message TEXT, status TEXT, timestamp TEXT, isread TEXT, type TEXT)";

	public ChatDatabase(Context con) {
		this.con = con;
		dbhelp = new DatabaseHelper(con);
	}

	public static ChatDatabase getInstance(Context c) // <-- added context as
														// parameter
	{
		synchronized (ChatDatabase.class) {
			if (instance == null) {
				instance = new ChatDatabase(c); // <-- used context in
												// constructor
			}
			return instance;
		}
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context con) {
			super(con, DATABASE, null, db_version);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_CHAT_HISTORY);

		}

		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		}
	}

	public ChatDatabase open() throws SQLException {
		DatabaseLite = dbhelp.getWritableDatabase();
		return this;
	}

	public void close()
	{
		dbhelp.close();
	}

	public void INSERT_NEW_MESSAGE(String userid, String receiverid,
			String message, String status, String timestamp, String isread,
			String type) {
		open();

		if (receiverid.contains("@"))
			receiverid = receiverid.substring(0, receiverid.indexOf("@"));

		if (receiverid.contains("jtcjtc"))
			receiverid = receiverid.replace("jtcjtc", "jtc");

		Log.d("Success", "Successfully Insert userid=" + userid);
		Log.d("Success", "Successfully Insert receiverid=" + receiverid);
		Log.d("Success", "Successfully Insert status=" + status);

		ContentValues initialval = new ContentValues();
		initialval.put("userid", userid);
		initialval.put("receiverid", receiverid);
		initialval.put("message", message);
		initialval.put("status", status);
		initialval.put("timestamp", timestamp);
		initialval.put("isread", isread);
		initialval.put("type",type);

		DatabaseLite.insert(TABLE_CHAT_HISTORY, null, initialval);

		Log.d("Success", "Successfully Insert");
		close();
	}

	// public void updateStatusofimage(String imageURI, Boolean status) {
	// open();
	//
	// ContentValues initialval = new ContentValues();
	// initialval.put("status", status);
	//
	// DatabaseLite.update(TABLE_CHAT_HISTORY, initialval, "imageuri='" +
	// imageURI
	// + "'", null);
	// close();
	//
	// }

	public ArrayList<ChatMessage> GetmessageForFixUser(String userid,
			String receiverid,String type) {

		if (receiverid.contains("@"))
			receiverid = receiverid.substring(0, receiverid.indexOf("@"));

		if (receiverid.contains("jtcjtc"))
			receiverid = receiverid.replace("jtcjtc", "jtc");

		Log.d("Success", "Successfully GetmessageForFixUser userid=" + userid);
		Log.d("Success", "Successfully GetmessageForFixUser receiverid="
				+ receiverid);

		// Log.i("Quesry=","select * from ChatHistory where userid='" + userid
		// + "' AND receiverid='" + receiverid+"'");
		String query = "select * from ChatHistory where userid='" + userid
				+ "' AND receiverid='" + receiverid +"' AND type='" + type + "'";

		ArrayList<ChatMessage> chatmessagelist = new ArrayList<ChatMessage>();
		open();

		String uid, rid, msg, stts, tmstmp, issread, ttype;
		Cursor c = DatabaseLite.rawQuery(query, null);
		// Cursor c = DatabaseLite.rawQuery(
		// "select * from ChatHistory where userid='" + userid
		// + "' AND receiverid='" + receiverid+"'", null);

		if (c.getCount() > 0) {

			c.moveToFirst();

			if (c.moveToFirst()) { // <--------------
				do {

					uid = c.getString(c.getColumnIndex("userid"));
					rid = c.getString(c.getColumnIndex("receiverid"));
					msg = c.getString(c.getColumnIndex("message"));
					stts = c.getString(c.getColumnIndex("status"));
					tmstmp = c.getString(c.getColumnIndex("timestamp"));
					issread = c.getString(c.getColumnIndex("isread"));
					ttype = c.getString(c.getColumnIndex("type"));

					Log.i("", "kkkkp userid==" + userid);
					Log.i("", "kkkkp receiverid==" + receiverid);
					Log.i("", "status==" +stts);

					boolean isIncoming = false;

					if (stts.equals("0")) {
						isIncoming = false;

					} else {
						isIncoming = true;
					}
					chatmessagelist.add(new ChatMessage(isIncoming, msg, uid,
							rid, tmstmp, issread, ttype));

				} while (c.moveToNext());
			}

		}

		Log.d("update pro response", "&%&  db count=" + c.getCount());
		Log.d("update pro response", "&%& remaining db count=" + c.getCount());

		close();
		c.close();
		updateEntry(userid,
				 receiverid, type);
		return chatmessagelist;
	}



	public ArrayList<ChatMessageNotification> GetConversationMessage(String userid) {
		// String query = "select count(*) from ChatHistory where userid='" +
		// userid + "' OR receiverid='" + userid
		// + "' AND type='false' group by receiverid";
		Log.i("ChatDatabase", "===userid===" + userid);
		ArrayList<ChatMessageNotification> mChatMessageNotification = new ArrayList<>();



//		String query = "SELECT COUNT(*) FROM " + "(select DISTINCT receiverid from ChatHistory where userid='" + userid
//				+ "' OR receiverid='" + userid + "' AND isread='false' AND type='" + type + "' group by receiverid)";

		// String query = "select count(*) from ChatHistory where userid='" +
		// userid + "' OR receiverid='" + userid
		// + "' AND type='false' group by receiverid";

		open();

		String selection = "userid= ?";
		String[] selectionArgs = {String.valueOf(userid)};

		Cursor c = DatabaseLite.query(true, "ChatHistory",
				new String[]{"userid", "receiverid", "message", "status", "timestamp", "isread", "type"},
				selection, selectionArgs, "receiverid", null, null, null);

		// Cursor c = DatabaseLite.rawQuery(query, null);

		int no = c.getCount();
		Log.i("ChatDatabase", "===Count===" + no);

		c.moveToFirst();

		for (int i = 0; i < c.getCount(); i++) {

			String uid, rid, msg, stts, tmstmp, issread, ttype, name,thumbnail;

			uid = c.getString(c.getColumnIndex("userid"));
			rid = c.getString(c.getColumnIndex("receiverid"));
			msg = c.getString(c.getColumnIndex("message"));
			stts = c.getString(c.getColumnIndex("status"));
			tmstmp = c.getString(c.getColumnIndex("timestamp"));
			issread = c.getString(c.getColumnIndex("isread"));
			ttype = c.getString(c.getColumnIndex("type"));

			Log.i("", "uid==" + uid);

			boolean isIncoming = false;

			isIncoming = !stts.equals("0");
			ChatMessage ctm = new ChatMessage(isIncoming, msg, uid, rid, tmstmp, issread, ttype);

			String query2 = "select * from ChatHistory where userid='" + userid + "' AND receiverid='" + rid
					+ "' AND isread='false'";

			Cursor c2 = DatabaseLite.rawQuery(query2, null);

			int no2 = c2.getCount();

			Log.i("ChatDatabase", "===userid==="  + rid + " : " + no2);
			Log.i("", "name===" + rid + " : " + no2);



			if(no2>0)
			mChatMessageNotification.add(new ChatMessageNotification("" + no2,"","", ctm));

			c.moveToNext();
		}

		close();
		c.close();
		return mChatMessageNotification;
	}




	public ArrayList<ChatMessage> GetUnreadmessageForFixUser(String userid,
													   String receiverid,String type)
	{
        open();



		if (receiverid.contains("@"))
			receiverid = receiverid.substring(0, receiverid.indexOf("@"));

		if (receiverid.contains("jtcjtc"))
			receiverid = receiverid.replace("jtcjtc", "jtc");

		Log.d("Success", "Successfully GetmessageForFixUser userid=" + userid);
		Log.d("Success", "Successfully GetmessageForFixUser receiverid="
				+ receiverid);

		// Log.i("Quesry=","select * from ChatHistory where userid='" + userid
		// + "' AND receiverid='" + receiverid+"'");
		String query = "select * from ChatHistory where userid='" + userid
				+ "'AND status='1' AND type='" + type + "' group by receiverid";

		ArrayList<ChatMessage> chatmessagelist = new ArrayList<ChatMessage>();


		String uid, rid, msg, stts, tmstmp, issread, ttype;
		Cursor c = DatabaseLite.rawQuery(query, null);
		// Cursor c = DatabaseLite.rawQuery(
		// "select * from ChatHistory where userid='" + userid
		// + "' AND receiverid='" + receiverid+"'", null);


		if (c.getCount() > 0) {

			c.moveToFirst();

			if (c.moveToFirst()) { // <--------------
				do {

					uid = c.getString(c.getColumnIndex("userid"));
					rid = c.getString(c.getColumnIndex("receiverid"));


                    String query1 = "select * from ChatHistory where userid='" + userid
                            + "'AND status='1' AND isread='false' AND type='" + type + "' AND receiverid='" +rid+"'";

                    Cursor c1 = DatabaseLite.rawQuery(query1, null);
                    System.out.println(" RECEIVER COUNT " + c1.getCount());

					msg = c.getString(c.getColumnIndex("message"));
					stts = c.getString(c.getColumnIndex("status"));
					tmstmp = c.getString(c.getColumnIndex("timestamp"));
					issread = c.getString(c.getColumnIndex("isread"));
					ttype = c.getString(c.getColumnIndex("type"));


                    System.out.println("    TIME STAMP  "+tmstmp);

					Log.d("", "kkkkp userid==" + userid);
					Log.d("", "kkkkp receiverid==" + receiverid);
					Log.d("", "status==" +stts);

					boolean isIncoming = false;

					if (stts.equals("0"))
                    {
						isIncoming = false;

					} else {
						isIncoming = true;
					}
					chatmessagelist.add(new ChatMessage(isIncoming, msg, uid,
							rid, tmstmp, issread, ttype+""+c1.getCount()));

				} while (c.moveToNext());
			}

		}

		Log.d("update pro response", "&%&  db count=" + c.getCount());
		Log.d("update pro response", "&%& remaining db count=" + c.getCount());

		close();
		c.close();

		return chatmessagelist;
	}

	// public List<String> getAllUser() {
	// boolean status = true;
	// String username;
	// List<String> userList = new ArrayList<String>();
	//
	// open();
	// Cursor c = DatabaseLite
	// .rawQuery("select * from ChatHistory where status='" + status
	// + "'", null);
	// if (c.getCount() > 0) {
	// c.moveToFirst();
	//
	// if (c.moveToFirst()) { // <--------------
	// do { // <---------if you not need the loop you can remove that
	// username = c.getString(c.getColumnIndex("imageuri"));
	// userList.add(username);
	// } while (c.moveToNext());
	// }
	//
	// }
	// c.close();
	//
	// return userList;
	// }

	public ArrayList<HashMap<String, String>> getAllImageHashMap() {

		String imguri, ldn, dtp, ddt, uni;
		boolean status = false;
		ArrayList<HashMap<String, String>> ImgList = new ArrayList<HashMap<String, String>>();

		open();
		Cursor c = DatabaseLite.rawQuery("select * from ChatHistory", null);
		// Cursor c =
		// DatabaseLite.rawQuery("select * from ChatHistory where status='"
		// + status+ "'", null);
		Log.d("copunt", "-" + c.getCount());
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			imguri = c.getString(c.getColumnIndex("imageuri"));
			ldn = c.getString(c.getColumnIndex("loadnumber"));
			dtp = c.getString(c.getColumnIndex("doctype"));
			ddt = c.getString(c.getColumnIndex("udate"));
			uni = c.getString(c.getColumnIndex("uniqueid"));
			map.put("imageuri", imguri);
			map.put("loadnumber", ldn);
			map.put("doctype", dtp);
			map.put("udate", ddt);
			map.put("uniqueid", uni);
			ImgList.add(map);
			c.moveToNext();
		}

		Log.d("copunt", "-" + c.getCount());

		c.close();

		close();
		return ImgList;
	}

	public void deleteRecord(String uniId)
	{
		open();
		DatabaseLite.execSQL("delete from ChatHistory where uniqueid='" + uniId
				+ "'");
		close();
	}


	public void deleteHistoryRecord(String userId,String receiverId)
	{
		open();
		DatabaseLite.execSQL("delete from ChatHistory where userid='"+userId
				+ "' and receiverid = '"+receiverId+"'");
		close();
	}

	public void updateEntry(String userid, String receiverid, String type) {

		open();
		ContentValues args = new ContentValues();
		args.put("isread", "true");

		DatabaseLite.update(TABLE_CHAT_HISTORY, args,  "userid='" + userid
				+ "' AND receiverid='" + receiverid +"' AND type='" + type + "'", null);

		close();
	}
	
	public int GetunreadMessageNumber(String userid,
			String receiverid,String type)
	{
		String query = "select * from ChatHistory where userid='" + userid
				+ "' AND receiverid='" + receiverid +"' AND type='" + type +"' AND isread='false'";

		ArrayList<ChatMessage> chatmessagelist = new ArrayList<ChatMessage>();
		open();

		String uid, rid, msg, stts, tmstmp, issread, ttype;
		Cursor c = DatabaseLite.rawQuery(query, null);
		// Cursor c = DatabaseLite.rawQuery(
		// "select * from ChatHistory where userid='" + userid
		// + "' AND receiverid='" + receiverid+"'", null);

		int no=c.getCount();

		close();
		c.close();
		return no;
	}



	public ArrayList<String> GetConversationMessageIDS(String userid) {
		// String query = "select count(*) from ChatHistory where userid='" +
		// userid + "' OR receiverid='" + userid
		// + "' AND type='false' group by receiverid";

		ArrayList<String> mIDS = new ArrayList<>();

//		String query = "SELECT COUNT(*) FROM " + "(select DISTINCT receiverid from ChatHistory where userid='" + userid
//				+ "' OR receiverid='" + userid + "' AND isread='false' AND type='" + type + "' group by receiverid)";

		// String query = "select count(*) from ChatHistory where userid='" +
		// userid + "' OR receiverid='" + userid
		// + "' AND type='false' group by receiverid";

		open();

		String selection = "userid= ?";
		String[] selectionArgs = {String.valueOf(userid)};

		Cursor c = DatabaseLite.query(true, "ChatHistory",
				new String[]{"userid", "receiverid", "message", "status", "timestamp", "isread", "type"},
				selection, selectionArgs, "receiverid", null, null, null);

		// Cursor c = DatabaseLite.rawQuery(query, null);

		int no = c.getCount();
		Log.i("ChatDatabase", "===userid===" + no);

		c.moveToFirst();

		for (int i = 0; i < c.getCount(); i++) {

			String uid, rid, msg, stts, tmstmp, issread, ttype;

			uid = c.getString(c.getColumnIndex("userid"));
			rid = c.getString(c.getColumnIndex("receiverid"));
			msg = c.getString(c.getColumnIndex("message"));
			stts = c.getString(c.getColumnIndex("status"));
			tmstmp = c.getString(c.getColumnIndex("timestamp"));
			issread = c.getString(c.getColumnIndex("isread"));
			ttype = c.getString(c.getColumnIndex("type"));

			Log.i("", "uid==" + uid);

			boolean isIncoming = false;

			isIncoming = !stts.equals("0");
			ChatMessage ctm = new ChatMessage(isIncoming, msg, uid, rid, tmstmp, issread, ttype);

			String query2 = "select * from ChatHistory where userid='" + userid + "' AND receiverid='" + rid
					+ "' AND isread='false'";

			Cursor c2 = DatabaseLite.rawQuery(query2, null);

			int no2 = c2.getCount();

			Log.i("ChatDatabase", "===userid==="  + rid + " : " + no2);
			Log.i("", "kpkpkp  name===" + rid + " : " + no2);



			if (rid.contains("cync_"))
				rid = rid.replace("cync_","");

			Log.i("ChatDatabase", "===rid===" + rid);

			if(no2>0)
			mIDS.add(rid);

			c.moveToNext();
		}

		close();
		c.close();
		return mIDS;
	}


}