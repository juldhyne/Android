package be.heh.juliendhyne.projetandroid.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import be.heh.juliendhyne.projetandroid.DB.User;
import be.heh.juliendhyne.projetandroid.R;

public class CustomAdapter extends ArrayAdapter<User> {

    Context context;

    TextView text;
    TextView mail;
    ImageView img;
    ImageButton edit;
    ImageButton delete;
    ImageButton password;
    ArrayList<User> users;


    private static LayoutInflater inflater = null;

    public CustomAdapter(Context context, ArrayList<User> users) {
        // TODO Auto-generated constructor stub
        super(context, 0, users);
        this.context = context;
        this.users = users;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false);
        }
        User user = getItem(position);
        int admin_count = 0;
        text = convertView.findViewById(R.id.name);
        mail = convertView.findViewById(R.id.mail);
        img = convertView.findViewById(R.id.user_lvl);
        edit = convertView.findViewById(R.id.edit_button);
        delete = convertView.findViewById(R.id.delete_button);
        password = convertView.findViewById(R.id.password_button);
        for (User user1:users) {
            if(user1.getLevel() == 2)admin_count++;
        }
        text.setText(user.getLastname() + " " + user.getFirstname());
        edit.setTag(position);
        delete.setTag(position);
        password.setTag(position);
        mail.setText(user.getEmail());
        if(admin_count < 2 && user.getLevel() == 2) {
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }
        img.setImageResource(user.getLevel() == 2 ?
                R.drawable.star_icon
                : R.drawable.user_pic);
        return convertView;

    }
}
