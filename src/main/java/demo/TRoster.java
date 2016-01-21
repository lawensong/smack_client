package demo;

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
        List<RosterEntry> entriesList = new ArrayList<>();
        RosterGroup rosterGroup = roster.getGroup(groupName);
        Collection<RosterEntry> rosterEntries = rosterGroup.getEntries();
        Iterator<RosterEntry> i = rosterEntries.iterator();
        while (i.hasNext()){
            entriesList.add(i.next());
        }
        return entriesList;
    }
}
