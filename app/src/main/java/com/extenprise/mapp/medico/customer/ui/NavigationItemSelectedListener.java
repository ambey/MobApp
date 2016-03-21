package com.extenprise.mapp.medico.customer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.activity.LoginActivity;
import com.extenprise.mapp.medico.customer.activity.PatientProfileActivity;
import com.extenprise.mapp.medico.customer.activity.PatientsHomeScreenActivity;
import com.extenprise.mapp.medico.customer.activity.SearchServProvActivity;
import com.extenprise.mapp.medico.customer.activity.ViewAppointmentListActivity;
import com.extenprise.mapp.medico.customer.activity.ViewRxListActivity;
import com.extenprise.mapp.medico.util.Utility;

/**
 * Created by ambey on 21/3/16.
 */
public class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
    private Activity activity;

    public NavigationItemSelectedListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.nav_home) {
            intent = new Intent(activity, PatientsHomeScreenActivity.class);
        } else if (id == R.id.nav_search) {
            intent = new Intent(activity, SearchServProvActivity.class);
            intent.putExtra("login", true);
        } else if (id == R.id.nav_logout) {
            Utility.prepareLogout(activity.getSharedPreferences("loginPrefs", Activity.MODE_PRIVATE));
            intent = new Intent(activity, LoginActivity.class);
        } else if (id == R.id.nav_view_app) {
            intent = new Intent(activity, ViewAppointmentListActivity.class);
        } else if (id == R.id.nav_view_rx) {
            intent = new Intent(activity, ViewRxListActivity.class);
        } else if (id == R.id.nav_profile) {
            intent = new Intent(activity, PatientProfileActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);

        activity.startActivity(intent);
        return true;
    }
}
