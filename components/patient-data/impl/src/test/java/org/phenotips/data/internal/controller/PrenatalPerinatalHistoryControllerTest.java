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
package org.phenotips.data.internal.controller;

import org.phenotips.data.PatientDataController;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for the {@link PrenatalPerinatalHistoryController} Component, only the overridden methods from
 * {@link AbstractComplexController} are tested here.
 */
public class PrenatalPerinatalHistoryControllerTest
{
    private static final String IVF = "ivf";

    private static final String ICSI = "icsi";

    private static final String ASSISTED_REPRODUCTION_IUI = "assistedReproduction_iui";

    private static final String ASSISTED_REPRODUCTION_FERTILITY_MEDS = "assistedReproduction_fertilityMeds";

    private static final String ASSISTED_REPRODUCTION_SURROGACY = "assistedReproduction_surrogacy";

    private static final String ASSISTED_REPRODUCTION_DONOR_EGG = "assistedReproduction_donoregg";

    private static final String ASSISTED_REPRODUCTION_DONOR_SPERM = "assistedReproduction_donorsperm";

    private static final String MULTIPLE_GESTATION = "multipleGestation";

    private static final String GESTATION_TWIN = "twinNumber";

    @Rule
    public MockitoComponentMockingRule<PatientDataController<String>> mocker =
        new MockitoComponentMockingRule<PatientDataController<String>>(PrenatalPerinatalHistoryController.class);

    @Test
    public void checkGetName() throws ComponentLookupException
    {
        Assert.assertEquals("prenatalPerinatalHistory", this.mocker.getComponentUnderTest().getName());
    }

    @Test
    public void checkGetJsonPropertyName() throws ComponentLookupException
    {
        Assert.assertEquals("prenatal_perinatal_history",
            ((AbstractComplexController<String>) this.mocker.getComponentUnderTest()).getJsonPropertyName());
    }

    @Test
    public void checkGetProperties() throws ComponentLookupException
    {
        List<String> result =
            ((AbstractComplexController<String>) this.mocker.getComponentUnderTest()).getProperties();

        Assert.assertEquals(10, result.size());
        Assert.assertThat(result, Matchers.hasItem("gestation"));
        Assert.assertThat(result, Matchers.hasItem(GESTATION_TWIN));
        Assert.assertThat(result, Matchers.hasItem(MULTIPLE_GESTATION));
        Assert.assertThat(result, Matchers.hasItem(IVF));
        Assert.assertThat(result, Matchers.hasItem(ICSI));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_IUI));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_FERTILITY_MEDS));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_SURROGACY));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_DONOR_EGG));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_DONOR_SPERM));
    }

    @Test
    public void checkGetBooleanFields() throws ComponentLookupException
    {
        List<String> result =
            ((AbstractComplexController<String>) this.mocker.getComponentUnderTest()).getBooleanFields();

        Assert.assertEquals(8, result.size());
        Assert.assertThat(result, Matchers.hasItem(MULTIPLE_GESTATION));
        Assert.assertThat(result, Matchers.hasItem(IVF));
        Assert.assertThat(result, Matchers.hasItem(ICSI));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_IUI));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_FERTILITY_MEDS));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_SURROGACY));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_DONOR_EGG));
        Assert.assertThat(result, Matchers.hasItem(ASSISTED_REPRODUCTION_DONOR_SPERM));
    }

    @Test
    public void checkGetCodeFields() throws ComponentLookupException
    {
        Assert.assertTrue(
            ((AbstractComplexController<String>) this.mocker.getComponentUnderTest()).getCodeFields().isEmpty());
    }
}
