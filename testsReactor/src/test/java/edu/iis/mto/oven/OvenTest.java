package edu.iis.mto.oven;

import static edu.iis.mto.oven.Oven.HEAT_UP_AND_FINISH_SETTING_TIME;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OvenTest {

    @Mock
    private Fan fanMock;

    @Mock
    private HeatingModule heatingModuleMock;

    @Test
    void shouldDontUseHeater() {

        Oven oven = new Oven(heatingModuleMock,fanMock);

        BakingProgram bakingProgram=BakingProgram.builder().build();


        oven.start(bakingProgram);

        verify(heatingModuleMock, times(0)).heater(HeatingSettings.builder()
                .withTargetTemp(bakingProgram.getInitialTemp())
                .withTimeInMinutes(HEAT_UP_AND_FINISH_SETTING_TIME)
                .build());


    }

}
