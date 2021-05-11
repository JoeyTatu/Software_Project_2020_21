package ncirl.x15015556.detect_diabetic_reinopathy;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

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

    public static void writeToFilePatientID(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("patientID.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

//    public static void writeToFileBitmap(Bitmap bitmap, Context context, int number) {
//        try {
//            String path = Environment.getExternalStorageDirectory().toString();
//            File file = new File(path, "DiabRet"+number+".png");
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//
//        } catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//            e.printStackTrace();
//        }
//    }

//    public static void writeArray(String filename, String[] data, Context context){
//        try {
//            FileOutputStream fileOutputStream  = context.openFileOutput(filename, Context.MODE_APPEND);
//            for (String s : data) {
//                fileOutputStream.write(s.getBytes());
//            }
//            fileOutputStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public static String convertStreamToString(InputStream is) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//        String line = null;
//        while ((line = reader.readLine()) != null){
//            sb.append(line).append("\n");
//        }
//        reader.close();
//
//        return sb.toString();
//    }
//
//    public static String getStringFromFile (String filePath) throws Exception {
//        File f1 = new File(filePath);
//        FileInputStream fin = new FileInputStream(f1);
//        String ret = convertStreamToString(fin);
//
//        fin.close();
//
//        return ret;
//    }

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

    public static String getResult(){
        int r = (int) (Math.random() * 5);
        String result = new String[] {"Stage I", "Stage II", "Stage III", "Stage IV", "Stage V"}[r];
        return result;
    }

    public static double getPercentage(){
        double min = 15;
        double max = 80;
        Random r = new Random();

        double random = min + (max - min) * r.nextDouble();
        Log.d("Random", String.valueOf(random));

        return round(random, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);

        return (double) tmp / factor;
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

    public static String readFromFilePatientID(Context context) {
        String patientID = "";

        try {
            InputStream inputStream = context.openFileInput("patientID.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                patientID = stringBuilder.toString();

            }

        } catch (FileNotFoundException e) {
            Log.e("ReadFromFile", "patientID.txt not found: " + e.toString());

        } catch (IOException e) {
            Log.e("ReadFromFile", "Cannot read file 'patientID.txt': " + e.toString());

        }
        return patientID;
    }

//    public static Bitmap readFromFileBitmap(Context context, int number) {
//        Bitmap image = null;
//        Uri uriImage = Uri.parse("DiabRet"+ number +".png");
//
//        try {
//            image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriImage);
//            if (image != null) {
//                return image;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
//
//        return image;
//    }
}
