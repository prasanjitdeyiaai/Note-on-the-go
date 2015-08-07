package com.pd.noteonthego.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by pradey on 8/5/2015.
 */
public class NoteSaveOnFileTask extends AsyncTask<Object, Void, Boolean> {

    Context context = null;
    @Override
    protected Boolean doInBackground(Object... params) {
        context = (Context) params[0];
        return writeToSDFile((String) params[1]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean) {
            // success
            Toast.makeText(context, "File saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // failed
            Toast.makeText(context, "File was not saved.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * write the note to a file
     * @param note
     * @return true is successful
     */
    private boolean writeToSDFile(String note) {
        File root = android.os.Environment.getExternalStorageDirectory();
        boolean wasFileSaved = true;

        File dir = new File(root.getAbsolutePath() + "/NoteOnTheGo");
        dir.mkdirs();

        // Random r = new Random();
        // int randNum = r.nextInt(9999 - 9001) + 9001;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = simpleDateFormat.format(new Date());


        String fileName = "note" + currentDateandTime;
        File file = new File(dir, fileName + ".txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(note);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            wasFileSaved = false;
        } catch (IOException e) {
            wasFileSaved = false;
            e.printStackTrace();
        }
        return wasFileSaved;
    }
}
