package health_app;

public class UserAuthenticator {
    DataPersistence persistence;

    public UserAuthenticator(DataPersistence persistence) {
        this.persistence = persistence;
    }

    public UserProfile login(String username) {
        return persistence.loadUserProfile(username);
    }
} 