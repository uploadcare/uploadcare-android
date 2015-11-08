package com.uploadcare.android.library.data;

import java.util.List;

public class ProjectData {

    public String name;

    public String pubKey;

    public List<CollaboratorData> collaborators;

    public class CollaboratorData {

        public String name;

        public String email;
    }

}