
package com.aman.chatgram.ui.adapters;

import android.graphics.Color;
import android.widget.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aman.chatgram.R;
import com.aman.chatgram.model.Chat;
import com.aman.chatgram.utility.CircularImageView;
import com.aman.chatgram.utility.CreateInitials;

import org.drinkless.td.libcore.telegram.TdApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Aman on 13-06-2016.
 */
public class ChatListAdapter extends BaseAdapter {
    private List<Chat> chatList;
    private Context context;
    private LayoutInflater inflater;

    public ChatListAdapter(Context context,List<Chat> chatList) {
        this.context=context;
        this.chatList=chatList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private void setChatImage(String imgUrl,String name,ViewHolder viewHolder){
        CircularImageView imageView= CreateInitials.createCircle(context,imgUrl,name);
        CreateInitials.createInitialsImage(imgUrl==null,name,viewHolder.txtInitials);
        viewHolder.imgUser.setImageDrawable(imageView);
        viewHolder.imgUser.setBackgroundColor(Color.TRANSPARENT);
    }
    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_chats, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imgUser = (ImageView) convertView.findViewById(R.id.img_user);
            viewHolder.txtInitials = (TextView) convertView.findViewById(R.id.txt_user_initials);
            viewHolder.txtUnreadMsgs = (TextView) convertView.findViewById(R.id.txt_unread_msgs);
            viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.txt_user_name);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            viewHolder.txtMsg = (TextView) convertView.findViewById(R.id.txt_msg);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TdApi.User user=null;
        Chat chat= (Chat) getItem(position);
        if(chat.getChat().type instanceof TdApi.PrivateChatInfo){
            user=((TdApi.PrivateChatInfo) chat.getChat().type).user;
            setChatImage(null,user.firstName+" "+user.lastName,viewHolder);
            viewHolder.txtUserName.setText(user.firstName+" "+user.lastName);
        }
        if(chat.getChat().unreadCount>0){
            viewHolder.txtUnreadMsgs.setText(chat.getChat().unreadCount);
        }
        viewHolder.txtUnreadMsgs.setVisibility(chat.getChat().unreadCount>0?View.VISIBLE:View.GONE);

        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("IST"), Locale.getDefault());
        calendar.setTimeInMillis(((long) chat.getChat().topMessage.date) * 1000L);
        Date date=calendar.getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
        dateFormat.setTimeZone(TimeZone.getDefault());
        viewHolder.txtTime.setText(dateFormat.format(date));

        if(chat.getChat().topMessage.message.getConstructor()==TdApi.MessageText.CONSTRUCTOR){
            viewHolder.txtMsg.setText(((TdApi.MessageText)chat.getChat().topMessage.message).text);
        }

        viewHolder.holdChat(chat);
        return convertView;
    }

    public class ViewHolder {
        public ImageView imgUser;
        public TextView txtInitials;
        public TextView txtUnreadMsgs;
        public TextView txtUserName;
        public TextView txtTime;
        public TextView txtMsg;
        public Chat chat;
        public Context context;

        public void holdChat(Chat chat){
            if(chat!=null){
                chat.onViewDetached(this);
            }
            this.chat=chat;
            chat.onViewAttached(this);
        }
    }
}
