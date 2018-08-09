package info.nightscout.androidaps.plugins.PumpCommon.hw.rileylink.ble.defs;
import org.apache.commons.lang3.NotImplementedException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public enum RileyLinkFirmwareVersion {
    Version_0_0(0, 0, "0.0"), // just for defaulting
    Version_0_9(0, 9, "0.9"), //
    Version_1_0(1, 0, "1.0"), //
    Version_2_0(2, 0, "2.0"), //
    Version_2_2(2, 2, "2.2"), //
    Version_3_0(3, 0, "3.0"), //
    UnknownVersion(0, 0, "???"), //
    Version1(Version_0_0, Version_0_9, Version_1_0), //
    Version2(Version_2_0, Version_2_2), //
    Version2AndHigher(Version_2_0, Version_2_2, Version_3_0), //
    ;
    private static final String FIRMWARE_IDENTIFICATION_PREFIX = "ubg_rfspy ";
    private static final Pattern _version_pattern = Pattern.compile(FIRMWARE_IDENTIFICATION_PREFIX +"([0-9]+)\\.([0-9]+)");
    private int major;
    private int minor;
    protected RileyLinkFirmwareVersion[] familyMembers;
    private String versionKey;
    static Map<String,RileyLinkFirmwareVersion> mapByVersion;
    static
    {
        mapByVersion = new HashMap<>();
        for (RileyLinkFirmwareVersion version : values()) {
            if (version.familyMembers==null)
            {
                mapByVersion.put(version.versionKey, version);
            }
        }
    }
    RileyLinkFirmwareVersion(int major, int minor, String versionKey) {
        this.major = major;
        this.minor = minor;
        this.versionKey = versionKey;
    }
    RileyLinkFirmwareVersion(RileyLinkFirmwareVersion...familyMembers) {
        this.familyMembers = familyMembers;
    }
    public static boolean isSameVersion(RileyLinkFirmwareVersion versionWeCheck, RileyLinkFirmwareVersion versionSources) {
        if (versionSources.familyMembers!=null) {
            for(RileyLinkFirmwareVersion vrs : versionSources.familyMembers) {
                if (vrs == versionWeCheck)
                    return true;
            }
        } else {
            return (versionWeCheck == versionSources);
        }
        return false;
    }

    public boolean isSameVersion(RileyLinkFirmwareVersion versionSources) {
        return isSameVersion(this, versionSources);
    }
    public RileyLinkFirmwareVersion getByVersionString(String versionString)
    {
        if (versionString != null) {
            Matcher m = _version_pattern.matcher(versionString);
            if (m.find()) {
                major = Integer.parseInt(m.group(1));
                minor = Integer.parseInt(m.group(2));
                String versionKey = major + "." + minor;
                if (mapByVersion.containsKey(versionKey))
                {
                    return mapByVersion.get(versionKey);
                }
                else
                {
                    return defaultToLowestMajorVersion(major); // just in case there is new release that we don't cover example: 2.3 etc
                    //throw new NotImplementedException(String.format("RileyLink firmware version %d.%dnot supported", major, minor));
                }
            }
        }
        return RileyLinkFirmwareVersion.UnknownVersion;
    }
    private RileyLinkFirmwareVersion defaultToLowestMajorVersion(int major) {
        if (mapByVersion.containsKey(major + ".0"))
        {
            return mapByVersion.get(major + ".0");
        }
        return UnknownVersion;
    }
    @Override
    public String toString() {
        return FIRMWARE_IDENTIFICATION_PREFIX + versionKey;
    }
}