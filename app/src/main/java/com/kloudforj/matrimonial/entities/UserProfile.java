package com.kloudforj.matrimonial.entities;

import java.util.List;

public class UserProfile
{

    public class Profile {

        private int user_id, age;
        private String first_name, middle_name, last_name, date_of_birth, sex;
        private String address1, address2, address3, country, state, city, pincode, phone_number;
        private int email_verified, phone_number_verified;
        private String caste, sub_caste1, sub_caste2;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getMiddle_name() {
            return middle_name;
        }

        public void setMiddle_name(String middle_name) {
            this.middle_name = middle_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getDate_of_birth() {
            return date_of_birth;
        }

        public void setDate_of_birth(String date_of_birth) {
            this.date_of_birth = date_of_birth;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAddress1() {
            return address1;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getAddress3() {
            return address3;
        }

        public void setAddress3(String address3) {
            this.address3 = address3;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public int getEmail_verified() {
            return email_verified;
        }

        public void setEmail_verified(int email_verified) {
            this.email_verified = email_verified;
        }

        public int getPhone_number_verified() {
            return phone_number_verified;
        }

        public void setPhone_number_verified(int phone_number_verified) {
            this.phone_number_verified = phone_number_verified;
        }

        public String getCaste() {
            return caste;
        }

        public void setCaste(String caste) {
            this.caste = caste;
        }

        public String getSub_caste1() {
            return sub_caste1;
        }

        public void setSub_caste1(String sub_caste1) {
            this.sub_caste1 = sub_caste1;
        }

        public String getSub_caste2() {
            return sub_caste2;
        }

        public void setSub_caste2(String sub_caste2) {
            this.sub_caste2 = sub_caste2;
        }
    }

    /*public class Education {

        private String[] education;

        public String[] getEducation() {
            return education;
        }

        public void setEducation(String[] education) {
            this.education = education;
        }
    }

    public class Hobby {

        private String[] hobby;

        public String[] getHobby() {
            return hobby;
        }

        public void setHobby(String[] hobby) {
            this.hobby = hobby;
        }
    }*/

    public class Images {

        private int id;
        private String image_path;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }
    }

    public class Family {

        private String father_name, father_education, father_profession, father_birth_place;
        private String mother_name, mother_education, mother_profession, mother_birth_place;
        private String siblings;

        public String getFather_name() {
            return father_name;
        }

        public void setFather_name(String father_name) {
            this.father_name = father_name;
        }

        public String getFather_education() {
            return father_education;
        }

        public void setFather_education(String father_education) {
            this.father_education = father_education;
        }

        public String getFather_profession() {
            return father_profession;
        }

        public void setFather_profession(String father_profession) {
            this.father_profession = father_profession;
        }

        public String getFather_birth_place() {
            return father_birth_place;
        }

        public void setFather_birth_place(String father_birth_place) {
            this.father_birth_place = father_birth_place;
        }

        public String getMother_name() {
            return mother_name;
        }

        public void setMother_name(String mother_name) {
            this.mother_name = mother_name;
        }

        public String getMother_education() {
            return mother_education;
        }

        public void setMother_education(String mother_education) {
            this.mother_education = mother_education;
        }

        public String getMother_profession() {
            return mother_profession;
        }

        public void setMother_profession(String mother_profession) {
            this.mother_profession = mother_profession;
        }

        public String getMother_birth_place() {
            return mother_birth_place;
        }

        public void setMother_birth_place(String mother_birth_place) {
            this.mother_birth_place = mother_birth_place;
        }

        public String getSiblings() {
            return siblings;
        }

        public void setSiblings(String siblings) {
            this.siblings = siblings;
        }
    }

    public class Extra {

        private String height, weight;
        private String birth_place, birth_time;
        private String current_job, about_me;

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getBirth_place() {
            return birth_place;
        }

        public void setBirth_place(String birth_place) {
            this.birth_place = birth_place;
        }

        public String getBirth_time() {
            return birth_time;
        }

        public void setBirth_time(String birth_time) {
            this.birth_time = birth_time;
        }

        public String getCurrent_job() {
            return current_job;
        }

        public void setCurrent_job(String current_job) {
            this.current_job = current_job;
        }

        public String getAbout_me() {
            return about_me;
        }

        public void setAbout_me(String about_me) {
            this.about_me = about_me;
        }
    }

    private int id;
    private Profile profile;
    private List<String> education;
    private List<String> hobbies;
    private Family family;
    private Extra extra;
    private Images images;

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<String> getEducation() {
        return education;
    }

    public void setEducation(List<String> education) {
        this.education = education;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }
}