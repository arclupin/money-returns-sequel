package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.Houseshares.Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;


public class MemberAdapter extends ArrayAdapter<Member> {
private ArrayList<Member> members;

    public MemberAdapter(Context context, int textViewResourceId, ArrayList<Member> objects) {
        super(context, textViewResourceId, objects);
        members = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
