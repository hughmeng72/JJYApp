package com.pekingopera.oa.common;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

/**
 * Created by wayne on 10/15/2016.
 */

public class FileHelper {
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getMineType(Context context, Uri uri) {
        String mineType = null;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getApplicationContext().getContentResolver();
            mineType = cr.getType(uri);
        }
        else {
            String ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString());

            mineType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
        }

        return mineType;
    }

}
