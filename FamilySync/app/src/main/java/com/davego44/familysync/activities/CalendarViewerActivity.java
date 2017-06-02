package com.davego44.familysync.activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.davego44.familysync.fragments.CalendarFragment;
import com.davego44.familysync.helper.Constants;
import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.fragments.EventViewFragment;
import com.davego44.familysync.adapters.FamilyMemberAdapter;
import com.davego44.familysync.dialogs.GroupDialog;
import com.davego44.familysync.adapters.NavAdapter;
import com.davego44.familysync.helper.NavItem;
import com.davego44.familysync.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class CalendarViewerActivity extends BaseActivity {

    private FamilyMemberAdapter familyMemberAdapter;
    private DrawerLayout drawerlayout;
    private DatabaseReference peopleInMyGroupRef;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;
    private GroupUser groupUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_viewer);
        groupUser = new GroupUser();
        peopleInMyGroupRef = baseFirebaseRef.child(Constants.Firebase.USERS).child(baseEncodedEmail).child(Constants.Firebase.GROUP);

        ListView navListView = (ListView) findViewById(R.id.navListView);
        NavAdapter<NavItem> navAdapter = new NavAdapter<NavItem>(CalendarViewerActivity.this, new ArrayList<NavItem>());
        if (navListView != null) {
            navListView.setAdapter(navAdapter);

            NavItem myProfileNavItem = new NavItem(R.mipmap.person, "My Profile");
            NavItem settingsNavItem = new NavItem(R.mipmap.settings, "Settings");
            navAdapter.add(myProfileNavItem);
            navAdapter.add(settingsNavItem);

            navListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    NavItem navItem = (NavItem) parent.getItemAtPosition(position);
                    switch (navItem.getTitle()) {
                        case "My Profile":
                            Toast.makeText(CalendarViewerActivity.this, "My profile and changing profile pictures is outside of scope", Toast.LENGTH_LONG).show();
                            break;
                        case "Settings":
                            Toast.makeText(CalendarViewerActivity.this, "Settings is outside of scope", Toast.LENGTH_SHORT).show();
                            break;
                        case "Manage":
                            DatabaseReference userRef = baseFirebaseRef.child(Constants.Firebase.USERS);
                            DatabaseReference myUserGroupRef = userRef.child(baseEncodedEmail).child("group");
                            GroupDialog groupDialog = new GroupDialog(CalendarViewerActivity.this, userRef, myUserGroupRef);
                            groupDialog.show();
                    }
                }
            });
        }

        ListView manageListView = (ListView) findViewById(R.id.manageListView);
        NavAdapter<NavItem> manageNavAdapter = new NavAdapter<NavItem>(CalendarViewerActivity.this, new ArrayList<NavItem>());
        if (manageListView != null) {
            manageListView.setAdapter(manageNavAdapter);
            NavItem manageNavItem = new NavItem(R.mipmap.group, "Manage");
            manageNavAdapter.add(manageNavItem);
            manageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DatabaseReference userRef = baseFirebaseRef.child(Constants.Firebase.USERS);
                    DatabaseReference myUserGroupRef = userRef.child(baseEncodedEmail).child("group");
                    GroupDialog groupDialog = new GroupDialog(CalendarViewerActivity.this, userRef, myUserGroupRef);
                    groupDialog.show();
                }
            });
        }

        ListView familyMembersListView = (ListView) findViewById(R.id.familyMembersListView);
        familyMemberAdapter = new FamilyMemberAdapter(CalendarViewerActivity.this, new ArrayList<GroupUser>());
        if (familyMembersListView != null) {
            familyMembersListView.setAdapter(familyMemberAdapter);

            familyMembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    groupUser = (GroupUser) parent.getItemAtPosition(position);
                    valueEventListener = peopleInMyGroupRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                DataSnapshot snapshot = iterator.next();
                                GroupUser gUser = snapshot.getValue(GroupUser.class);
                                if (gUser.equals(groupUser)) {
                                    DatabaseReference db = snapshot.getRef().child("show");
                                    if (groupUser.getShow()){
                                        db.setValue(false);
                                        groupUser.setShow(false);
                                    }
                                    else {
                                        db.setValue(true);
                                        groupUser.setShow(true);
                                    }
                                    break;
                                }
                            }
                            peopleInMyGroupRef.removeEventListener(valueEventListener);
                            familyMemberAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //changedSelected(groupUser);
                    //familyMemberAdapter.notifyDataSetChanged();
                }
            });
        }

        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*drawerToggle = new ActionBarDrawerToggle(this, drawerlayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                MenuItem item = menu.findItem(R.id.action_arrow);
                item.setIcon(R.mipmap.left_arrow_icon);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                MenuItem item = menu.findItem(R.id.action_arrow);
                item.setIcon(R.mipmap.right_arrow_icon);
            }

        };
        drawerlayout.addDrawerListener(drawerToggle);*/

        LinearLayout logoutView = (LinearLayout) findViewById(R.id.logoutView);
        if (logoutView != null) {
            logoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    baseFirebaseAuth.signOut();
                }
            });
        }

        if (savedInstanceState == null) {
            CalendarFragment calendarFragment = new CalendarFragment();
            EventViewFragment eventViewFragment = new EventViewFragment();
            calendarFragment.setGroupVariables(baseEncodedEmail, baseFirebaseRef);
            eventViewFragment.setGroupVariables(baseEncodedEmail, baseFirebaseRef);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_top_container, calendarFragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_bottom_container, eventViewFragment).commit();
        }

        setupFirebaseListener();
    }

    public void setBottomFragmentDate(Date date) {
        ((EventViewFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_bottom_container)).setDate(date);
    }

    public Date getBottomFragmentDate() {
        return ((EventViewFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_bottom_container)).getDate();
    }

    public void zoomInOut() {
        CalendarFragment calendarFragment = (CalendarFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_top_container);
        if (calendarFragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().show(calendarFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(calendarFragment).commit();
        }
    }

    private void setupFirebaseListener() {
        childEventListener = peopleInMyGroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                familyMemberAdapter.add(groupUser);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                familyMemberAdapter.removeGroupUser(groupUser);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method that inflates the action bar
     *
     * @param menu For the MenuInflater
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        //this.menu = menu;
        return true;
    }

    /**
     * Method that sets up the actions of the action bar at the top
     *
     * @param item For the grabbing of what action was selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_add_event:
                Intent intent = new Intent(CalendarViewerActivity.this, AddEventActivity.class);
                intent.putExtra(Constants.Intent.DATE, getBottomFragmentDate().getTime());
                startActivity(intent);
                return true;

            case R.id.action_search:
                Toast.makeText(CalendarViewerActivity.this, "The Search Function is outside the scope", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_arrow:
                if (drawerlayout.isDrawerOpen(GravityCompat.END)) {
                    drawerlayout.closeDrawer(GravityCompat.END);
                    //item.setIcon(R.mipmap.left_arrow_icon);
                } else {
                    drawerlayout.openDrawer(GravityCompat.END);
                    //item.setIcon(R.mipmap.right_arrow_icon);
                }
                return true;

            /*case R.id.action_log_out:
                bFirebaseAuth.signOut();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that runs when the activity is finished
     */
    @Override
    public void onDestroy() {
        if (childEventListener != null)
            peopleInMyGroupRef.removeEventListener(childEventListener);
        super.onDestroy();
    }
}
