package com.artemis.managers;


/**
 * Deprecated, as it is much cleaner to use GenericGroupManager instead
 * with an Enum as a type.
 * 
 * This is a concrete implementation of a Generic Group Manager that uses
 * Strings to identify groups.
 * 
 * If you need to group your entities together, e.g. tanks going into "units"
 * group or explosions into "effects", then use this manager. You must
 * retrieve it using world instance.
 * 
 * A entity can be assigned to more than one group.
 * 
 * @author Arni Arent
 *
 */
@Deprecated
public class GroupManager extends GenericGroupManager<String> {
}
