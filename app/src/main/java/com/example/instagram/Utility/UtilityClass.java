package com.example.instagram.Utility;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class UtilityClass {

    public static String getFileExtension(Context mContext, Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mContext.getContentResolver().getType(imageUri));
    }

}
