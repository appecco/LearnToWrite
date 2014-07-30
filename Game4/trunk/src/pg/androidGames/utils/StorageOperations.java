package pg.androidGames.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class StorageOperations {

	/*
	 * Usage: saveDataToPreferencesFile(context, "scores", new String [] {"score", "3"});
	 */
	public static void saveDataToPreferencesFile(Context context, String filename, String...data) {
        SharedPreferences prefs = context.getSharedPreferences(filename, 0);
        SharedPreferences.Editor editor = prefs.edit();     
        for(int i = 0; i < data.length; i += 2) {
            editor.putString(data[i], data[i + 1]); 
        }
        editor.commit();
	}
	
	/*
	 * Usage: readDataFromPreferencesFile(context, "scores", "score");
	 */
	public static String readDataFromPreferencesFile(Context context, String filename, String key, String defaultValue) {
        SharedPreferences prefs =  context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }
	
	public static JSONObject loadExternalJson(String filePath, String standardDirectory) throws IOException{
		return loadJson(Environment.getExternalStoragePublicDirectory(standardDirectory),
				filePath);
	}

	public static JSONObject loadDataJson(String filePath) throws IOException{
		return loadJson(Environment.getDataDirectory(),
				filePath);
	}

	public static JSONObject loadAssetsJson(Context context, String filePath) throws IOException{
		JSONObject json = null;
		InputStream is = context.getAssets().open(filePath);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        try {
			json = new JSONObject(new String(buffer, "UTF-8"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json;
	}
	
	private static JSONObject loadJson(File directory, String filePath) throws IOException {
		JSONObject json = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			try {
				File file = new File(directory, filePath);
				if (!file.exists()){
					throw new IOException("The requested file does not exist - " + filePath);
				}
				FileInputStream stream = new FileInputStream(file);
				String jsonStr = null;
				try {
					FileChannel fc = stream.getChannel();
					MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
							fc.size());
	
					jsonStr = Charset.defaultCharset().decode(bb).toString();
				} finally {
					stream.close();
				}
				json = new JSONObject(jsonStr);
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
}
