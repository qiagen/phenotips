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
package org.phenotips.studies.family.rest;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource for working with family pedigree, identified by their internal PhenoTips identifier.
 *
 * @version $Id$
 * @since 1.4
 */
@Path("/familyPedigree")
public interface FamilyPedigreeResource
{
    /**
     * Retrieve a family pedigree record, identified by its internal PhenoTips identifier, in its JSON representation.
     * If the indicated family record doesn't exist, or if the user sending the request doesn't have the right to view
     * the target family record, an error is returned.
     *
     * @param id the family's internal identifier, see {@link org.phenotips.studies.family.Family#getId()}
     * @return the JSON representation of the requested family, or a status message in case of error
     */
    @Path("/{entity-id}/inJSON")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Response getFamilyPedigreeInJSON(@PathParam("entity-id") String id);

    /**
     * Retrieve a family pedigree, identified by its internal PhenoTips identifier, in its SVG-XML representation.
     * If the indicated family record doesn't exist, or if the user sending the request doesn't have the right to view
     * the target family record, an error is returned.
     *
     * @param id the family's internal identifier, see {@link org.phenotips.studies.family.Family#getId()}
     * @return the SVG-XML representation of the requested family, or a status message in case of error
     */
    @Path("/{entity-id}/inSVG")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    Response getFamilyPedigreeInSVG(@PathParam("entity-id") String id);

    /**
     * Create a new family record owned by the user.
     *
     * @param userName the name of the user
     * @return the id of the newly created family if the operation was successful, or an error report otherwise
     */
    @Path("/create/{user_name}")
    @PUT
    Response createFamily(@PathParam("user_name") String userName);

}

