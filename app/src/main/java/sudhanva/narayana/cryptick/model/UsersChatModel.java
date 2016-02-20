package sudhanva.narayana.cryptick.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UsersChatModel implements Parcelable {

    public static final Creator<UsersChatModel> CREATOR = new Creator<UsersChatModel>() {
        @Override
        public UsersChatModel createFromParcel(Parcel parcel) {
            return new UsersChatModel(parcel);
        }

        @Override
        public UsersChatModel[] newArray(int size) {
            return new UsersChatModel[size];
        }
    };
    /*recipient info*/
    private String firstName;
    private String provider; //if you don't include this app crash
    private String userEmail;
    private String createdAt;
    private String connection;
    private int avatarId;
    private String mRecipientUid;
    /*Current user (or sender) info*/
    private String mCurrentUserName;
    private String mCurrentUserUid;
    private String mCurrentUserEmail;
    private String mCurrentUserCreatedAt;

    public UsersChatModel() {
        //required empty username
    }

    private UsersChatModel(Parcel parcelIn) {

        //Remember the order used to read data is the same used to write them
        firstName = parcelIn.readString();
        provider = parcelIn.readString();
        userEmail = parcelIn.readString();
        createdAt = parcelIn.readString();
        connection = parcelIn.readString();
        avatarId = parcelIn.readInt();
        mRecipientUid = parcelIn.readString();
        mCurrentUserName = parcelIn.readString();
        mCurrentUserUid = parcelIn.readString();
        mCurrentUserEmail = parcelIn.readString();
        mCurrentUserCreatedAt = parcelIn.readString();

    }

    /*Recipient info*/
    public String getFirstName() {
        return firstName;
    }

    public String getUserEmail() {
        //Log.e("user email  ", userEmail);
        return userEmail;
    }

    public String getProvider() {
        return provider;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getConnection() {
        return connection;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public String getRecipientUid() {
        return mRecipientUid;
    }

    public void setRecipientUid(String givenUserUid) {
        mRecipientUid = givenUserUid;
    }

    public String getCurrentUserName() {
        return mCurrentUserName;
    }

    /*Current user (or sender) info*/
    public void setCurrentUserName(String currentUserName) {
        mCurrentUserName = currentUserName;
    }

    public String getCurrentUserEmail() {
        //Log.e("current user email  ", mCurrentUserEmail);
        return mCurrentUserEmail;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        mCurrentUserEmail = currentUserEmail;
    }

    public String getCurrentUserCreatedAt() {
        return mCurrentUserCreatedAt;
    }

    public void setCurrentUserCreatedAt(String currentUserCreatedAt) {
        mCurrentUserCreatedAt = currentUserCreatedAt;
    }

    public String getCurrentUserUid() {
        return mCurrentUserUid;
    }

    public void setCurrentUserUid(String currentUserUid) {
        mCurrentUserUid = currentUserUid;
    }

    /*create chat endpoint for firebase*/
    public String getChatRef() {
        return createUniqueChatRef();
    }

    private String createUniqueChatRef() {
        String uniqueChatRef = "";
        if (createdAtCurrentUser() > createdAtRecipient()) {
            uniqueChatRef = cleanEmailAddress(getCurrentUserEmail()) + "-" + cleanEmailAddress(getUserEmail());
        } else {

            uniqueChatRef = cleanEmailAddress(getUserEmail()) + "-" + cleanEmailAddress(getCurrentUserEmail());
        }
        return uniqueChatRef;
    }

    private long createdAtCurrentUser() {
        return Long.parseLong(getCurrentUserCreatedAt());
    }

    private long createdAtRecipient() {
        return Long.parseLong(getCreatedAt());
    }


    /*Parcelable*/

    private String cleanEmailAddress(String email) {

        //replace dot with comma since firebase does not allow dot
        return email.replace(".", "-");

    }

    @Override
    public int describeContents() {
        return 0; //ignore
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        //Store information using parcel method
        //the order for writing and reading must be the same
        parcel.writeString(firstName);
        parcel.writeString(provider);
        parcel.writeString(userEmail);
        parcel.writeString(createdAt);
        parcel.writeString(connection);
        parcel.writeInt(avatarId);
        parcel.writeString(mRecipientUid);
        parcel.writeString(mCurrentUserName);
        parcel.writeString(mCurrentUserUid);
        parcel.writeString(mCurrentUserEmail);
        parcel.writeString(mCurrentUserCreatedAt);

    }
}

