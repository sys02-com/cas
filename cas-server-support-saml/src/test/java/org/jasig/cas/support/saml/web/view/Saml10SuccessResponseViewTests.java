/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.support.saml.web.view;

import org.jasig.cas.CasProtocolConstants;
import org.jasig.cas.TestUtils;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.RememberMeCredential;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.services.DefaultServicesManagerImpl;
import org.jasig.cas.services.InMemoryServiceRegistryDaoImpl;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.RegisteredServiceImpl;
import org.jasig.cas.services.ReturnAllAttributeReleasePolicy;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.support.saml.authentication.SamlAuthenticationMetaDataPopulator;
import org.jasig.cas.validation.Assertion;
import org.jasig.cas.validation.ImmutableAssertion;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link Saml10SuccessResponseView} class.
 *
 * @author Scott Battaglia
 * @author Marvin S. Addison
 * @since 3.1
 *
 */
public class Saml10SuccessResponseViewTests {

    private Saml10SuccessResponseView response;

    @Before
    public void setUp() throws Exception {

        final List<RegisteredService> list = new ArrayList<RegisteredService>();

        final RegisteredServiceImpl regSvc = new RegisteredServiceImpl();
        regSvc.setServiceId(TestUtils.getService().getId());
        regSvc.setEnabled(true);
        regSvc.setName("Test Service");
        regSvc.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());

        list.add(regSvc);
        final InMemoryServiceRegistryDaoImpl dao = new InMemoryServiceRegistryDaoImpl();
        dao.setRegisteredServices(list);
        final ServicesManager servicesManager = new DefaultServicesManagerImpl(dao);
        this.response = new Saml10SuccessResponseView();
        this.response.setServicesManager(servicesManager);
        this.response.setIssuer("testIssuer");
        this.response.setIssueLength(1000);
    }

    @Test
    public void testResponse() throws Exception {
        final Map<String, Object> model = new HashMap<String, Object>();

        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("testAttribute", "testValue");
        attributes.put("testEmptyCollection", Collections.emptyList());
        attributes.put("testAttributeCollection", Arrays.asList(new String[] {"tac1", "tac2"}));
        final SimplePrincipal principal = new SimplePrincipal("testPrincipal", attributes);

        final Map<String, Object> authAttributes = new HashMap<String, Object>();
        authAttributes.put(
                SamlAuthenticationMetaDataPopulator.ATTRIBUTE_AUTHENTICATION_METHOD,
                SamlAuthenticationMetaDataPopulator.AUTHN_METHOD_SSL_TLS_CLIENT);
        authAttributes.put("testSamlAttribute", "value");

        final Authentication primary = TestUtils.getAuthentication(principal, authAttributes);
        final Assertion assertion = new ImmutableAssertion(
                primary, Collections.singletonList(primary), TestUtils.getService(), true);
        model.put("assertion", assertion);

        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        this.response.renderMergedOutputModel(model, new MockHttpServletRequest(), servletResponse);
        final String written = servletResponse.getContentAsString();

        assertTrue(written.contains("testPrincipal"));
        assertTrue(written.contains("testAttribute"));
        assertTrue(written.contains("testValue"));
        assertFalse(written.contains("testEmptyCollection"));
        assertTrue(written.contains("testAttributeCollection"));
        assertTrue(written.contains("tac1"));
        assertTrue(written.contains("tac2"));
        assertTrue(written.contains(SamlAuthenticationMetaDataPopulator.AUTHN_METHOD_SSL_TLS_CLIENT));
        assertTrue(written.contains("AuthenticationMethod"));
        assertTrue(written.contains("AssertionID"));
    }

    @Test
    public void testResponseWithNoAttributes() throws Exception {
        final Map<String, Object> model = new HashMap<String, Object>();

        final SimplePrincipal principal = new SimplePrincipal("testPrincipal");

        final Map<String, Object> authAttributes = new HashMap<String, Object>();
        authAttributes.put(
                SamlAuthenticationMetaDataPopulator.ATTRIBUTE_AUTHENTICATION_METHOD,
                SamlAuthenticationMetaDataPopulator.AUTHN_METHOD_SSL_TLS_CLIENT);
        authAttributes.put("testSamlAttribute", "value");

        final Authentication primary = TestUtils.getAuthentication(principal, authAttributes);

        final Assertion assertion = new ImmutableAssertion(
                primary, Collections.singletonList(primary), TestUtils.getService(), true);
        model.put("assertion", assertion);

        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        this.response.renderMergedOutputModel(model, new MockHttpServletRequest(), servletResponse);
        final String written = servletResponse.getContentAsString();

        assertTrue(written.contains("testPrincipal"));
        assertTrue(written.contains(SamlAuthenticationMetaDataPopulator.AUTHN_METHOD_SSL_TLS_CLIENT));
        assertTrue(written.contains("AuthenticationMethod="));
    }

    @Test
    public void testResponseWithoutAuthMethod() throws Exception {
        final Map<String, Object> model = new HashMap<String, Object>();

        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("testAttribute", "testValue");
        final SimplePrincipal principal = new SimplePrincipal("testPrincipal", attributes);

        final Map<String, Object> authnAttributes = new HashMap<String, Object>();
        authnAttributes.put("authnAttribute1", "authnAttrbuteV1");
        authnAttributes.put("authnAttribute2", "authnAttrbuteV2");
        authnAttributes.put(RememberMeCredential.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME, Boolean.TRUE);

        final Authentication primary = TestUtils.getAuthentication(principal, authnAttributes);

        final Assertion assertion = new ImmutableAssertion(
                primary, Collections.singletonList(primary), TestUtils.getService(), true);
        model.put("assertion", assertion);

        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        this.response.renderMergedOutputModel(model, new MockHttpServletRequest(), servletResponse);
        final String written = servletResponse.getContentAsString();

        assertTrue(written.contains("testPrincipal"));
        assertTrue(written.contains("testAttribute"));
        assertTrue(written.contains("testValue"));
        assertTrue(written.contains("authnAttribute1"));
        assertTrue(written.contains("authnAttribute2"));
        assertTrue(written.contains(CasProtocolConstants.VALIDATION_REMEMBER_ME_ATTRIBUTE_NAME));
        assertTrue(written.contains("urn:oasis:names:tc:SAML:1.0:am:unspecified"));
    }
}
