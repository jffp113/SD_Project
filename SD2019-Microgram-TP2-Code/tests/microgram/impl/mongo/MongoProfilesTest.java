package microgram.impl.mongo;

import microgram.api.Profile;
import microgram.api.java.Result;

import java.util.List;

import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test Needs mongodb online with a new empty instace
 */
class MongoProfilesTest {
    final Profile p1 = new Profile("jffp113","Jorge Pereira","facebook.com/photo1");
    final Profile p2 = new Profile("elComp","Pedro Campones","facebook.com/photo2");
    final Profile p3 = new Profile("smd","Sergio Duarte","facebook.com/photo3");

    final Profile p4 = new Profile("jffpereira","Jorge Filipe Ferreira Pereira","facebook.com/photo4");
    final Profile p5 = new Profile("jfpereira","Jorge Filipe Pereira","facebook.com/photo5");
    final Profile p6 = new Profile("MariaGata","Maria","facebook.com/photo6");
    final Profile p7 = new Profile("MarioGato","Mario","facebook.com/photo7");
    final Profile p8 = new Profile("joana","Joana ","facebook.com/photo8");


    public MongoProfiles profiles = new MongoProfiles();;


    @org.junit.jupiter.api.AfterEach
    void tearDown() {

    }

    @org.junit.jupiter.api.Test
    void createAndGetAndDeleteProfile() {
        //Create Profiles
        Result<Void> r1 = profiles.createProfile(p1);
        Result<Void> r2 = profiles.createProfile(p2);
        Result<Void> r3 = profiles.createProfile(p3);

        assertTrue(r1.isOK());
        assertTrue(r2.isOK());
        assertTrue(r3.isOK());

        //Get Profiles
        Result<Profile> pr1 =  profiles.getProfile(p1.getUserId());
        Result<Profile> pr2 =  profiles.getProfile(p2.getUserId());
        Result<Profile> pr3 =  profiles.getProfile(p3.getUserId());

        assertTrue(pr1.isOK());
        assertTrue(pr2.isOK());
        assertTrue(pr3.isOK());

        assertEquals("Jorge Pereira", pr1.value().getFullName());
        assertEquals("Pedro Campones", pr2.value().getFullName());
        assertEquals("Sergio Duarte", pr3.value().getFullName());

        //Delete 2 Profiles 2 Times
        Result<Void> r1d = profiles.deleteProfile(p1.getUserId());
        Result<Void> r1dr = profiles.deleteProfile(p1.getUserId());
        Result<Void> r2d =profiles.deleteProfile(p2.getUserId());
        Result<Void> r2dr =profiles.deleteProfile(p2.getUserId());

        assertTrue(r1d.isOK());
        assertTrue(r2d.isOK());
        assertEquals(Result.ErrorCode.NOT_FOUND,r1dr.error());
        assertEquals(Result.ErrorCode.NOT_FOUND,r2dr.error());

        //Insert Existing Profile
        r3 = profiles.createProfile(p3);
        assertEquals(CONFLICT,r3.error());

        //Delete Last Profile

        Result<Void> r3d = profiles.deleteProfile(p3.getUserId());
        assertTrue(r3d.isOK());

    }



    @org.junit.jupiter.api.Test
    void search() {
        profiles.createProfile(p4);
        profiles.createProfile(p5);
        profiles.createProfile(p6);
        profiles.createProfile(p7);
        profiles.createProfile(p8);

        Result<List<Profile>> r = profiles.search("j");
        assertEquals(3,r.value().size());

         r = profiles.search("jf");
        assertEquals(2,r.value().size());

        r = profiles.search("Mari");
        assertEquals(2,r.value().size());

        r = profiles.search("jff");
        assertEquals(1,r.value().size());

        profiles.deleteProfile("jffpereira");
        profiles.deleteProfile("jfpereira");
        profiles.deleteProfile("MariaGata");
        profiles.deleteProfile("MarioGato");
        profiles.deleteProfile("joana");
    }

    @org.junit.jupiter.api.Test
    void followAndisFollowing() {
        profiles.createProfile(p4);
        profiles.createProfile(p5);
        profiles.createProfile(p6);
        profiles.createProfile(p7);
        profiles.createProfile(p8);

        Result<Boolean> rb = profiles.isFollowing(p4.getUserId(),p5.getUserId());
        assertFalse(rb.value());

        Result<Void> r = profiles.follow(p4.getUserId(),p5.getUserId(),true);
        assertTrue(r.isOK());
        r = profiles.follow(p4.getUserId(),p5.getUserId(),true);
        assertEquals(CONFLICT,r.error());

        rb = profiles.isFollowing(p4.getUserId(),p5.getUserId());
        assertTrue(rb.value());

        profiles.deleteProfile("jffpereira");
        profiles.deleteProfile("jfpereira");
        profiles.deleteProfile("MariaGata");
        profiles.deleteProfile("MarioGato");
        profiles.deleteProfile("joana");
    }
}