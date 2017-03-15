package com.android.udl.locationoffers.domain;

/**
 * Created by ubuntu on 15/03/17.
 */

public class PlacesInterestEnumTranslator {
    public static int translate(String type){
        int value;
        switch(type){
            case "TYPE_ACCOUNTING":
                value = 1; break;
            case "TYPE_AIRPORT":
                value = 2; break;
            case "TYPE_AMUSEMENT_PARK":
                value = 3; break;
            case "TYPE_AQUARIUM":
                value = 4; break;
            case "TYPE_ART_GALLERY":
                value = 5; break;
            case "TYPE_ATM":
                value = 6; break;
            case "TYPE_BAKERY":
                value = 7; break;
            case "TYPE_BANK":
                value = 8; break;
            case "TYPE_BAR":
                value = 9; break;
            case "TYPE_BEAUTY_SALON":
                value = 10; break;
            case "TYPE_BICYCLE_STORE":
                value = 11; break;
            case "TYPE_BOOK_STORE":
                value = 12; break;
            case "TYPE_BOWLING_ALLEY":
                value = 13; break;
            case "TYPE_BUS_STATION":
                value = 14; break;
            case "TYPE_CAFE":
                value = 15; break;
            case "TYPE_CAMPGROUND":
                value = 16; break;
            case "TYPE_CAR_DEALER":
                value = 17; break;
            case "TYPE_CAR_RENTAL":
                value = 18; break;
            case "TYPE_CAR_REPAIR":
                value = 19; break;
            case "TYPE_CAR_WASH":
                value = 20; break;
            case "TYPE_CASINO":
                value = 21; break;
            case "TYPE_CEMETERY":
                value = 22; break;
            case "TYPE_CHURCH":
                value = 23; break;
            case "TYPE_CITY_HALL":
                value = 24; break;
            case "TYPE_CLOTHING_STORE":
                value = 25; break;
            case "TYPE_CONVENIENCE_STORE":
                value = 26; break;
            case "TYPE_COURTHOUSE":
                value = 27; break;
            case "TYPE_DENTIST":
                value = 28; break;
            case "TYPE_DEPARTMENT_STORE":
                value = 29; break;
            case "TYPE_DOCTOR":
                value = 30; break;
            case "TYPE_ELECTRICIAN":
                value = 31; break;
            case "TYPE_ELECTRONICS_STORE":
                value = 32; break;
            case "TYPE_EMBASSY":
                value = 33; break;
            case "TYPE_ESTABLISHMENT":
                value = 34; break;
            case "TYPE_FINANCE":
                value = 35; break;
            case "TYPE_FIRE_STATION":
                value = 36; break;
            case "TYPE_FLORIST":
                value = 37; break;
            case " TYPE_FOOD":
                value = 38; break;
            case "TYPE_FUNERAL_HOME":
                value = 39; break;
            case "TYPE_FURNITURE_STORE":
                value = 40; break;
            case "TYPE_GAS_STATION":
                value = 41; break;
            case "TYPE_GENERAL_CONTRACTOR":
                value = 42; break;
            case "TYPE_GROCERY_OR_SUPERMARKET":
                value = 43; break;
            case "TYPE_GYM":
                value = 44; break;
            case "TYPE_HAIR_CARE":
                value = 45; break;
            case "TYPE_HARDWARE_STORE":
                value = 46; break;
            case "TYPE_HEALTH":
                value = 47; break;
            case "TYPE_HINDU_TEMPLE":
                value = 48; break;
            case "TYPE_HOME_GOODS_STORE":
                value = 49; break;
            case "TYPE_HOSPITAL":
                value = 50; break;
            case "TYPE_INSURANCE_AGENCY":
                value = 51; break;
            case "TYPE_JEWELRY_STORE":
                value = 52; break;
            case "TYPE_LAUNDRY":
                value = 53; break;
            case "TYPE_LAWYER":
                value = 54; break;
            case "TYPE_LIBRARY":
                value = 55; break;
            case "TYPE_LIQUOR_STORE":
                value = 56; break;
            case "TYPE_LOCAL_GOVERNMENT_OFFICE":
                value = 57; break;
            case "TYPE_LOCKSMITH":
                value = 58; break;
            case "TYPE_LODGING":
                value = 59; break;
            case "TYPE_MEAL_DELIVERY":
                value = 60; break;
            case "TYPE_MEAL_TAKEAWAY":
                value = 61; break;
            case "TYPE_MOSQUE":
                value = 62; break;
            case "TYPE_MOVIE_RENTAL":
                value = 63; break;
            case "TYPE_MOVIE_THEATER":
                value = 64; break;
            case "TYPE_MOVING_COMPANY":
                value = 65; break;
            case "TYPE_MUSEUM":
                value = 66; break;
            case "TYPE_NIGHT_CLUB":
                value = 67; break;
            case "TYPE_OTHER":
                value = 0; break;
            case "TYPE_PAINTER":
                value = 68; break;
            case "TYPE_PARK":
                value = 69; break;
            case "TYPE_PARKING":
                value = 70; break;
            case "TYPE_PET_STORE":
                value = 71; break;
            case "TYPE_PHARMACY":
                value = 72; break;
            case "TYPE_PHYSIOTHERAPIST":
                value = 73; break;
            case "TYPE_PLACE_OF_WORSHIP":
                value = 74; break;
            case "TYPE_PLUMBER":
                value = 75; break;
            case "TYPE_POINT_OF_INTEREST":
                value = 1013; break;
            case "TYPE_POLICE":
                value = 76; break;
            case "TYPE_POST_OFFICE":
                value = 77; break;
            case "TYPE_PREMISE":
                value = 1018; break;
            case "TYPE_REAL_ESTATE_AGENCY":
                value = 78; break;
            case "TYPE_RESTAURANT":
                value = 79; break;
            case "TYPE_ROOFING_CONTRACTOR":
                value = 80; break;
            case "TYPE_ROOM":
                value = 1019; break;
            case "TYPE_RV_PARK":
                value = 81; break;
            case "TYPE_SCHOOL":
                value = 82; break;
            case "TYPE_SHOE_STORE":
                value = 83; break;
            case "TYPE_SHOPPING_MALL":
                value = 84; break;
            case "TYPE_SPA":
                value = 85; break;
            case "TYPE_STADIUM":
                value = 86; break;
            case "TYPE_STORAGE":
                value = 87; break;
            case "TYPE_STORE":
                value = 88; break;
            case "TYPE_SUBWAY_STATION":
                value = 89; break;
            case "TYPE_SYNAGOGUE":
                value = 90; break;
            case "TYPE_TAXI_STAND":
                value = 91; break;
            case "TYPE_TRAIN_STATION":
                value = 92; break;
            case "TYPE_TRAVEL_AGENCY":
                value = 93; break;
            case "TYPE_UNIVERSITY":
                value = 94; break;
            case "TYPE_VETERINARY_CARE":
                value = 95; break;
            case "TYPE_ZOO":
                value = 96; break;
            default:
                value = 0; break;
        }
        return value;
    }
}
