/*
* @References:
* Global variables: Vishal Yidav - https://stackoverflow.com/questions/1944656/android-global-variable
* Global variables: NCI Students in WhatsApp Group helped here. Suggested to add getters/setters, otherwise code would not work.
*/

package ncirl.x15015556.detect_diabetic_reinopathy;

// Global Variables
public class Global {
    public static String facebookID;
    public static String googleID;
    public static Boolean signedIn;

    public Global() {
        this.facebookID = facebookID;
        this.googleID = googleID;
        this.signedIn = signedIn;
    }

    public static String getFacebookID() {
        return facebookID;
    }

    public static void setFacebookID(String facebookID) {
        Global.facebookID = facebookID;
    }

    public static String getGoogleID() {
        return googleID;
    }

    public static void setGoogleID(String googleID) {
        Global.googleID = googleID;
    }

    public static Boolean getSignedIn() {
        return signedIn;
    }

    public static void setSignedIn(Boolean signedIn) {
        Global.signedIn = signedIn;
    }
}
