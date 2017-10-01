package com.davidinchina.showcode.readingview.view;

import java.util.List;

/**
 * author:davidinchina on 2017/10/1 14:39
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:例句实体
 */
public class SentenceResult {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 443808
         * user : {"username":"username","nickname":"nickanme","id":72196,"avatar":"http://qstatic.shanbay.com/team/media_store/d99aa28621d300c251c67d4c2ede08e8_1.jpg"}
         * unlikes : 0
         * likes : 3
         * translation : 跟大家打个招呼
         * annotation : say <vocab>hello</vocab> to everybody
         * version : 0
         */

        private int id;
        private UserBean user;
        private int unlikes;
        private int likes;
        private String translation;
        private String annotation;
        private int version;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public int getUnlikes() {
            return unlikes;
        }

        public void setUnlikes(int unlikes) {
            this.unlikes = unlikes;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }

        public String getAnnotation() {
            return annotation;
        }

        public void setAnnotation(String annotation) {
            this.annotation = annotation;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public static class UserBean {
            /**
             * username : username
             * nickname : nickanme
             * id : 72196
             * avatar : http://qstatic.shanbay.com/team/media_store/d99aa28621d300c251c67d4c2ede08e8_1.jpg
             */

            private String username;
            private String nickname;
            private int id;
            private String avatar;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
