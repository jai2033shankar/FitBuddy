package de.avalax.fitbuddy.workout.basic;


import de.avalax.fitbuddy.workout.Set;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class BasicSetTest {
    Set set;

    @Before
    public void setUp() throws Exception {
        set = new BasicSet(12.5);
    }

    @Test
    public void BasicSet_ShouldGetWeight() throws Exception {
        assertThat(set.getWeight(),equalTo(12.5));
    }
    @Test
    public void BasicSet_ShouldGetRepetitions() throws Exception {
        set.setRepetitions(15);
        assertThat(set.getRepetitions(), equalTo(15));
    }
}