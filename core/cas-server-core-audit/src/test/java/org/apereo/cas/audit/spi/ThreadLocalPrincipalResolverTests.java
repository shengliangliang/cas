package org.apereo.cas.audit.spi;

import org.apereo.cas.authentication.CurrentCredentialsAndAuthentication;
import org.apereo.inspektr.common.spi.PrincipalResolver;
import org.junit.After;
import org.junit.Test;

import static org.apereo.cas.authentication.CoreAuthenticationTestUtils.CONST_USERNAME;
import static org.apereo.cas.authentication.CoreAuthenticationTestUtils.getAuthentication;
import static org.apereo.cas.authentication.CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword;
import static org.junit.Assert.*;

/**
 * Unit test for {@link ThreadLocalPrincipalResolver}.
 *
 * @author Dmitriy Kopylenko
 * @since 5.0.0
 */
public class ThreadLocalPrincipalResolverTests {

    private final ThreadLocalPrincipalResolver theResolver =
            new ThreadLocalPrincipalResolver(new PrincipalIdProvider() {});

    @After
    public void cleanup() {
        CurrentCredentialsAndAuthentication.clear();
    }

    @Test
    public void noAuthenticationOrCrendentialsAvailableInThreadLocal() {
        assertResolvedPrincipal(PrincipalResolver.UNKNOWN_USER);
    }

    @Test
    public void singleThreadSetsSingleCredential() {
        CurrentCredentialsAndAuthentication.bindCurrent(getCredentialsWithSameUsernameAndPassword());
        assertResolvedPrincipal(CONST_USERNAME);
    }

    @Test
    public void singleThreadSetsMultipleCredentials() {
        CurrentCredentialsAndAuthentication.bindCurrent(
                getCredentialsWithSameUsernameAndPassword(),
                getCredentialsWithSameUsernameAndPassword("test2"));

        assertResolvedPrincipal(String.format("%s, %s", CONST_USERNAME, "test2"));
    }

    @Test
    public void singleThreadSetsAuthentication() {
        CurrentCredentialsAndAuthentication.bindCurrent(getAuthentication());
        assertResolvedPrincipal(CONST_USERNAME);
    }

    private void assertResolvedPrincipal(final String principalId) {
        assertEquals(principalId, theResolver.resolveFrom(null, (Object) null));
    }
}
