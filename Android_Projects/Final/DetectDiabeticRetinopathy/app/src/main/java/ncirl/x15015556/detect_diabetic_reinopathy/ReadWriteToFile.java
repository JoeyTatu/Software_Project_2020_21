package ncirl.x15015556.detect_diabetic_reinopathy;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ReadWriteToFile {

    public static void writeToFileGoogleID(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("googleID.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeToFileGoogleName(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("googleName.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFileGoogleID(Context context) {
        String googleID = "";

        try {
            InputStream inputStream = context.openFileInput("googleID.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                googleID = stringBuilder.toString();
                Global.setGoogleID(googleID);

            }

        } catch (FileNotFoundException e) {
            Log.e("ReadFromFile", "googleID.txt not found: " + e.toString());

        } catch (IOException e) {
            Log.e("ReadFromFile", "Cannot read file 'googleID.txt': " + e.toString());

        }
        return googleID;
    }

    public static String readFromFileGoogleName(Context context) {
        String googleName = "";

        try {
            InputStream inputStream = context.openFileInput("googleName.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                googleName = stringBuilder.toString();

            }

        } catch (FileNotFoundException e) {
            Log.e("ReadFromFile", "googleName.txt not found: " + e.toString());

        } catch (IOException e) {
            Log.e("ReadFromFile", "Cannot read file 'googleName.txt': " + e.toString());

        }
        return googleName;
    }
}
