package com.aman.chatgram.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.aman.chatgram.R;

/**
 * Created by Aman on 13-06-2016.
 */
public class CreateInitials {
    public static CircularImageView createCircle(Context context, String image, String name) {
        Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        if (image != null && !image.equals("")) {
            bitmap = BitmapFactory.decodeFile(image);
        } else {
            String[] bgColors = context.getResources().getStringArray(R.array.image_bg_colors);
            String contactColor = bgColors[Math.abs(name.hashCode() % (bgColors.length - 1))];
            bitmap.eraseColor(Color.parseColor(contactColor));
        }
        return new CircularImageView(bitmap);
    }

    public static void createInitialsImage(boolean visibility, String name, TextView initials) {
        if (visibility) {
            initials.setVisibility(View.VISIBLE);
            String[] userName = name.toUpperCase().split(" ");
            initials.setText(userName[0].substring(0, 1));
            if (userName.length > 1) {
                initials.setText(initials.getText() + userName[1].substring(0, 1));
            }
        } else {
            initials.setVisibility(View.GONE);
        }
    }
}
