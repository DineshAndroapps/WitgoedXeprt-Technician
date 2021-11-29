package com.witgoedxpert.technician.Helper;

/*-------------------------------

    - woopy -

    Created by cubycode @2017
    All Rights reserved

-------------------------------*/

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class Configs extends Application {


    // THIS IS THE RED MAIN COLOR OF THIS APP
    public static String MAIN_COLOR = "#fa334a";


    // YOU CAN CHANGE THE CURRENCY SYMBOL AS YOU WISH
    public static String CURRENCY = "$";


    // DECLARE FONT FILES (the font files are located into "app/src/main/assets/font" folder)
    public static Typeface qsBold, qsLight, qsRegular, titBlack, titLight, titRegular, titSemibold;


    // THIS IS THE MAX DURATION OF A VIDEO RECORDING FOR AN AD (in seconds)
    public static int MAXIMUM_DURATION_VIDEO = 10;


    // YOU CAN CHANGE THE DEFAULT LOCATION COORDINATES FOR ADS AS YOU WISH BY EDITING THE FLOAT VALUES BELOW:


    // YOU CAN CHANGE THE AD REPORT OPTIONS BELOW AS YOU WISH
    public static String[] reportAdOptions = {
            "Prohibited item",
            "Conterfeit",
            "Wrong category",
            "Keyword spam",
            "Repeated post",
            "Nudity/pornography/mature content",
            "Hateful speech/blackmail",
    };


    // YOU CAN CHANGE THE USER REPORT OPTIONS BELOW AS YOU WISH
    public static String[] reportUserOptions = {
            "Selling counterfeit items",
            "Selling prohibited items",
            "Items wrongly categorized",
            "Nudity/pornography/mature content",
            "Keyword spammer",
            "Hateful speech/blackmail",
            "Suspected fraudster",
            "No-show on meetup",
            "Backed out of deal",
            "Touting",
            "Spamming",
    };


    /***********  DO NOT EDIT THE CODE BELOW!! **********/

    public static String USER_CLASS_NAME = "_User";
    public static String USER_USERNAME = "username";
    public static String USER_EMAIL = "email";
    public static String USER_EMAIL_VERIFIED = "emailVerified";
    public static String USER_FULLNAME = "fullName";
    public static String USER_AVATAR = "avatar";
    public static String USER_LOCATION = "location";
    public static String USER_ABOUT_ME = "aboutMe";
    public static String USER_WEBSITE = "website";
    public static String USER_IS_REPORTED = "isReported";
    public static String USER_REPORT_MESSAGE = "reportMessage";
    public static String USER_HAS_BLOCKED = "hasBlocked";

    public static String CATEGORIES_CLASS_NAME = "Categories";
    public static String CATEGORIES_CATEGORY = "category";
    public static String CATEGORIES_IMAGE = "image";

    public static String SUBCATEGORIES_CLASS_NAME = "Subcategories";
    public static String SUBCATEGORIES_SUBCATEGORY = "subcategory";
    public static String SUBCATEGORIES_CATEGORY = "category";

    public static String ADS_CLASS_NAME = "Ads";
    public static String ADS_SELLER_POINTER = "sellerPointer";
    public static String ADS_LIKED_BY = "likedBy"; // Array
    public static String ADS_KEYWORDS = "keywords"; // Array
    public static String ADS_TITLE = "title";
    public static String ADS_PRICE = "price";
    public static String ADS_CURRENCY = "currency";
    public static String ADS_CATEGORY = "category";
    public static String ADS_SUBCATEGORY = "subcategory";
    public static String ADS_LOCATION = "location";
    public static String ADS_IMAGE1 = "image1";
    public static String ADS_IMAGE2 = "image2";
    public static String ADS_IMAGE3 = "image3";
    public static String ADS_VIDEO = "video";
    public static String ADS_VIDEO_THUMBNAIL = "videoThumbnail";
    public static String ADS_DESCRIPTION = "description";
    public static String ADS_CONDITION = "condition";
    public static String ADS_LIKES = "likes";
    public static String ADS_COMMENTS = "comments";
    public static String ADS_IS_REPORTED = "isReported";
    public static String ADS_REPORT_MESSAGE = "reportMessage";
    public static String ADS_CREATED_AT = "createdAt";


    public static String LIKES_CLASS_NAME = "Likes";
    public static String LIKES_CURR_USER = "currUser";
    public static String LIKES_AD_LIKED = "adLiked";
    public static String LIKES_CREATED_AT = "createdAt";

    public static String COMMENTS_CLASS_NAME = "Comments";
    public static String COMMENTS_USER_POINTER = "userPointer";
    public static String COMMENTS_AD_POINTER = "adPointer";
    public static String COMMENTS_COMMENT = "comment";
    public static String COMMENTS_CREATED_AT = "createdAt";

    public static String ACTIVITY_CLASS_NAME = "Activity";
    public static String ACTIVITY_CURRENT_USER = "currUser";
    public static String ACTIVITY_OTHER_USER = "otherUser";
    public static String ACTIVITY_TEXT = "text";
    public static String ACTIVITY_CREATED_AT = "createdAt";


    public static String INBOX_CLASS_NAME = "Inbox";
    public static String INBOX_AD_POINTER = "adPointer";
    public static String INBOX_SENDER = "sender";
    public static String INBOX_RECEIVER = "receiver";
    public static String INBOX_INBOX_ID = "inboxID";
    public static String INBOX_MESSAGE = "message";
    public static String INBOX_IMAGE = "image";
    public static String INBOX_CREATED_AT = "createdAt";

    public static String CHATS_CLASS_NAME = "Chats";
    public static String CHATS_LAST_MESSAGE = "lastMessage";
    public static String CHATS_USER_POINTER = "userPointer";
    public static String CHATS_OTHER_USER = "otherUser";
    public static String CHATS_ID = "chatID";
    public static String CHATS_AD_POINTER = "adPointer";
    public static String CHATS_CREATED_AT = "createdAt";

    public static String FEEDBACKS_CLASS_NAME = "Feedbacks";
    public static String FEEDBACKS_AD_TITLE = "adTitle";
    public static String FEEDBACKS_SELLER_POINTER = "sellerPointer";
    public static String FEEDBACKS_REVIEWER_POINTER = "reviewerPointer";
    public static String FEEDBACKS_STARS = "stars";
    public static String FEEDBACKS_TEXT = "text";


    /* Global Variables */
    public static float distanceInMiles = (float) 50;
    public static String IMAGE_FORMAT = ".jpg";
    // For query pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE_THRESHOLD = 3;

    boolean isParseInitialized = false;

    private static Configs instance;

    public static Configs getInstance() {
        return instance;
    }



    // MARK: - SCALE BITMAP TO MAX SIZE ---------------------------------------
    public static Bitmap scaleBitmapToMaxSize(int maxSize, Bitmap bm) {
        int outWidth;
        int outHeight;
        int inWidth = bm.getWidth();
        int inHeight = bm.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        return Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
    }


    // MARK: - GET TIME AGO SINCE DATE ------------------------------------------------------------
    public static String timeAgoSinceDate(Date date) {
        String dateString = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            String sentStr = (df.format(date));
            Date past = df.parse(sentStr);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                dateString = seconds + " seconds ago";
            } else if (minutes < 60) {
                dateString = minutes + " minutes ago";
            } else if (hours < 24) {
                dateString = hours + " hours ago";
            } else {
                dateString = days + " days ago";
            }
        } catch (Exception j) {
            j.printStackTrace();
        }
        return dateString;
    }


    // ROUND THOUSANDS NUMBERS ------------------------------------------
    public static String roundThousandsIntoK(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }


    // MARK: - SIMPLE ALERT ------------------------------


    // MARK: - LOGIN ALERT ------------------------------


    // Method to get URI of a stored image
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
    }
}// @end
