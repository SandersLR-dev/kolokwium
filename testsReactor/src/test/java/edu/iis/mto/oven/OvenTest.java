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

import java.util.List;

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

    @Test
    void shouldThrowOvenExceptionWithThermoCirculation() throws HeatingException {

        Oven oven = new Oven(heatingModuleMock,fanMock);

        ProgramStage stage1=ProgramStage.builder().withStageTime(10).withTargetTemp(120).withHeat(HeatType.THERMO_CIRCULATION).build();
        ProgramStage stage2=ProgramStage.builder().withStageTime(10).withTargetTemp(140).withHeat(HeatType.HEATER).build();

        List<ProgramStage> list=List.of(stage1,stage2);

        BakingProgram bakingProgram=BakingProgram.builder()
                .withInitialTemp(100)
                .withStages(list).build();


        doThrow(HeatingException.class).when(heatingModuleMock).termalCircuit(HeatingSettings.builder().withTargetTemp(120).withTimeInMinutes(10).build());

        assertThrows(OvenException.class,()->oven.start(bakingProgram));


    }



}
