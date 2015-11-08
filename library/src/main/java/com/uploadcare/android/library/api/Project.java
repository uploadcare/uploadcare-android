package com.uploadcare.android.library.api;

import com.uploadcare.android.library.data.ProjectData;

import java.util.ArrayList;
import java.util.List;

/**
 * The resource for project, associated with the connecting account.
 */
public class Project {

    private final UploadcareClient client;

    private final ProjectData projectData;

    public Project(UploadcareClient client, ProjectData projectData) {
        this.client = client;
        this.projectData = projectData;
    }

    public String getName() {
        return projectData.name;
    }

    public String getPubKey() {
        return projectData.pubKey;
    }

    public Collaborator getOwner() {
        if (projectData.collaborators.size() > 0) {
            return new Collaborator(this, projectData.collaborators.get(0));
        } else {
            return null;
        }
    }

    public List<Collaborator> getCollaborators() {
        ArrayList<Collaborator> collaborators = new ArrayList<Collaborator>(
                projectData.collaborators.size());
        for (ProjectData.CollaboratorData collaboratorData : projectData.collaborators) {
            collaborators.add(new Collaborator(this, collaboratorData));
        }
        return collaborators;
    }


    public class Collaborator {

        private final Project project;

        private final ProjectData.CollaboratorData collaboratorData;

        private Collaborator(Project project, ProjectData.CollaboratorData collaboratorData) {
            this.project = project;
            this.collaboratorData = collaboratorData;
        }

        public String getName() {
            return collaboratorData.name;
        }

        public String getEmail() {
            return collaboratorData.email;
        }
    }
}