/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.studies.family.rest.internal;

import org.phenotips.rest.Autolinker;
import org.phenotips.security.authorization.AuthorizationService;
import org.phenotips.studies.family.Family;
import org.phenotips.studies.family.FamilyRepository;
import org.phenotips.studies.family.FamilyTools;
import org.phenotips.studies.family.Pedigree;
import org.phenotips.studies.family.PedigreeProcessor;
import org.phenotips.studies.family.rest.FamilyPedigreeResource;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceResolver;
import org.xwiki.rest.XWikiResource;
import org.xwiki.security.authorization.Right;
import org.xwiki.users.User;
import org.xwiki.users.UserManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * Default implementation for {@link FamilyPedigreeResource} using XWiki's support for REST resources.
 *
 * @version $Id$
 * @since 1.4
 */
@Component
@Named("org.phenotips.studies.family.rest.internal.DefaultFamilyPedigreeResourceImpl")
@Singleton
public class DefaultFamilyPedigreeResourceImpl extends XWikiResource implements FamilyPedigreeResource
{
    private static final String NO_SUCH_FAMILY_ERROR_MESSAGE = "No such family record: [{}]";

    @Inject
    private Logger logger;

    @Inject
    private FamilyRepository repository;

    @Inject
    private AuthorizationService access;

    @Inject
    private UserManager users;

    @Inject
    private PedigreeProcessor pedigreeProcessor;

    @Inject
    private Provider<Autolinker> autolinker;

    @Inject
    private FamilyTools familyTools;

    /** Fills in missing reference fields with those from the current context document to create a full reference. */
    @Inject
    @Named("current")
    private EntityReferenceResolver<EntityReference> currentResolver;

    @Override
    public Response getFamilyPedigreeInJSON(String id)
    {
        this.logger.warn("Retrieving family pedigree in JSON [{}] via REST", id);
        Pedigree pedigree = getFamilyPedigree(id);
        JSONObject json = pedigree.getData();
        return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Override
    public Response getFamilyPedigreeInSVG(String id)
    {
        this.logger.warn("Retrieving family pedigree in SVG [{}] via REST", id);
        Pedigree pedigree = getFamilyPedigree(id);
        String svg = pedigree.getImage(pedigree.getProbandId());
        return Response.ok(svg, MediaType.APPLICATION_SVG_XML).build();
    }

    @Override
    public Response createFamily(String userName)
    {
        User currentUser = this.users.getCurrentUser();
        if (!this.access.hasAccess(currentUser == null ? null : currentUser, Right.EDIT,
                this.currentResolver.resolve(Family.DATA_SPACE, EntityType.SPACE))) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        try {
            Family family = this.familyTools.createFamilyForUser(userName);

            JSONObject response = new JSONObject();
            response.put("userName", userName);
            response.put("familyId", family.getId());

            return Response.ok(response, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception ex) {
            this.logger.error("Could not process family creation request: {}", ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Pedigree getFamilyPedigree(String id)
    {
        Family family = this.repository.getFamilyById(id);
        if (family == null) {
            this.logger.warn(NO_SUCH_FAMILY_ERROR_MESSAGE, id);
            Response.status(Response.Status.NOT_FOUND).build();
            return null;
        }
        User currentUser = this.users.getCurrentUser();
        if (!this.access.hasAccess(currentUser, Right.VIEW, family.getDocumentReference())) {
            this.logger.error("View access denied to user [{}] on family record [{}]", currentUser, id);
            Response.status(Response.Status.FORBIDDEN).build();
            return null;
        }
        return family.getPedigreeInSimpleJson();
    }
}

