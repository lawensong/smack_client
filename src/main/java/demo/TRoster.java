package demo;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/1/21.
 */
public class TRoster {

    public List<RosterGroup> getGroup(Roster roster){
        List<RosterGroup> groupList = new ArrayList<RosterGroup>();
        Collection<RosterGroup> rosterGroups = roster.getGroups();
        Iterator<RosterGroup> i = rosterGroups.iterator();
        while (i.hasNext()){
            groupList.add(i.next());
        }
        return groupList;
    }

    public boolean addGroup(Roster roster, String groupName){
        try{
            roster.createGroup(groupName);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<RosterEntry> getEntriesByGroup(Roster roster, String groupName){
        List<RosterEntry> entriesList = new ArrayList<RosterEntry>();
        RosterGroup rosterGroup = roster.getGroup(groupName);
        Collection<RosterEntry> rosterEntries = rosterGroup.getEntries();
        Iterator<RosterEntry> i = rosterEntries.iterator();
        while (i.hasNext()){
            entriesList.add(i.next());
        }
        return entriesList;
    }

    public List<RosterEntry> getAllEntry(Roster roster){
        List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
        Collection<RosterEntry> rosterEntries = roster.getEntries();
        Iterator<RosterEntry> i = rosterEntries.iterator();
        while (i.hasNext()){
            EntriesList.add(i.next());
        }
        return EntriesList;
    }

    public boolean addUser(Roster roster, String userName, String name){
        try{
            roster.createEntry(userName, name, null);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUserGroup(Roster roster, String userName, String name, String groupName){
        try {
            roster.createEntry(userName, name, new String[]{groupName});
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeUser(Roster roster, String userName){
        try {
            if(userName.contains("@")){
                userName = userName.split("@")[0];
            }

            RosterEntry entry = roster.getEntry(userName);
            System.out.println("remove user");
            roster.removeEntry(entry);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
