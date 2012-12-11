package com.example.createrequestlist;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductDBHelper extends SQLiteOpenHelper {
	
	private final String DB_FILE_PATH;								//データベースが保存されているパス
	private static final String DB_NAME = "product";				//データベース名
	private static final String DB_NAME_ASSET = "product.sqlite3";	//事前に作成したデータベースファイル
	private final Context context;									//呼び出し元のアクティビティ保持用

	public ProductDBHelper(Context con){
		//データベースを開く
		super(con, DB_NAME,null,1);
		
		//アクティビティ保持
		this.context = con;
		
		//端末上のデータベースパス名を取得
		this.DB_FILE_PATH = (this.context.getDatabasePath(DB_NAME)).toString();
	}
	
	public void createEmptyDataBase(){
		//データベース存在チェック処理へ
		boolean dbCheck = this.checkDataBaseExists();
		if(dbCheck){
			//既にデータベースファイルが作成されているため何もしない
		}else{
			//アプリのデフォルトシステムパスにデータベースファイルが作成される
			this.getReadableDatabase();
			try{
				//データベースコピー処理へ
				this.copyDataBaseFromAsset();
			}catch(IOException e){
				Log.e("copyDataBaseFromAsset","データベースコピー処理が失敗しました");
			}
		}
	}
	
	//既にある場合に、上書きコピーを防止するため
	private boolean checkDataBaseExists(){
		//チェック用データベース初期化
		SQLiteDatabase check_DB = null;
		
		try{
			//指定したデータベースを書き込みモードで開く
			check_DB = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
		}catch(SQLiteException e){
			//データベースはまだ存在しない
		}
		
		//チェック用データベースを閉じる
		if(check_DB != null){
			check_DB.close();
			return true;
		}
		return false;
	}
	
	private void copyDataBaseFromAsset() throws IOException{
		
		//assets 内のデータベースファイルにアクセス(読み込み元)
		InputStream in = this.context.getAssets().open(DB_NAME_ASSET);
		
		//作成されたデータベースファイルにアクセス(書き込み先)
		OutputStream out = new FileOutputStream(this.DB_FILE_PATH);
		
		//読み込み元の内容を書き込み先にコピーする
		byte[] buffer = new byte[1024];
		int size;
		while((size = in.read(buffer)) > 0){
			out.write(buffer, 0, size); 
		}
		
		//クローズ処理
		//out.flush(); //BufferedWriteStream等のBuffered・・・を使用した場合に必要
		out.close();
		in.close();
	}
	
	public SQLiteDatabase openDataBase(){
		
		//データベースを書き込みモードで開く
		return SQLiteDatabase.openDatabase(this.DB_FILE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
