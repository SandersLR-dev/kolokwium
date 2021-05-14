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

    @Test
    void shouldThrowOvenExceptionWithGrill() throws HeatingException {

        Oven oven = new Oven(heatingModuleMock,fanMock);

        ProgramStage stage1=ProgramStage.builder().withStageTime(10).withTargetTemp(120).withHeat(HeatType.THERMO_CIRCULATION).build();
        ProgramStage stage2=ProgramStage.builder().withStageTime(10).withTargetTemp(140).withHeat(HeatType.GRILL).build();


        List<ProgramStage> list=List.of(stage1,stage2);

        BakingProgram bakingProgram=BakingProgram.builder()
                .withInitialTemp(100)
                .withStages(list).build();

        doThrow(HeatingException.class).when(heatingModuleMock).grill(HeatingSettings.builder().withTargetTemp(140).withTimeInMinutes(10).build());

        assertThrows(OvenException.class,()->oven.start(bakingProgram));


    }

    @Test
    void shouldRunHeaterOneTimeWithOnFan()  {

        Oven oven = new Oven(heatingModuleMock,fanMock);

        ProgramStage stage1=ProgramStage.builder().withStageTime(10).withTargetTemp(120).withHeat(HeatType.THERMO_CIRCULATION).build();
        ProgramStage stage2=ProgramStage.builder().withStageTime(10).withTargetTemp(160).withHeat(HeatType.HEATER).build();

        List<ProgramStage> list=List.of(stage1,stage2);

        BakingProgram bakingProgram=BakingProgram.builder()
                .withInitialTemp(100)
                .withStages(list).build();

        when(fanMock.isOn()).thenReturn(true);

        oven.start(bakingProgram);

        verify(heatingModuleMock,times(1)).heater(HeatingSettings.builder().withTimeInMinutes(10).withTargetTemp(160).build());
        verify(fanMock,times(2)).off();
        verify(fanMock,times(1)).on();


    }

    @Test
    void shouldRunHeaterTwoTimesGrillTwoTimesAndThermalCirculationThreeTimes() throws HeatingException {

        Oven oven = new Oven(heatingModuleMock,fanMock);

        ProgramStage stage1=ProgramStage.builder().withStageTime(10).withTargetTemp(120).withHeat(HeatType.THERMO_CIRCULATION).build();
        ProgramStage stage2=ProgramStage.builder().withStageTime(10).withTargetTemp(160).withHeat(HeatType.HEATER).build();
        ProgramStage stage3=ProgramStage.builder().withStageTime(10).withTargetTemp(180).withHeat(HeatType.GRILL).build();
        ProgramStage stage4=ProgramStage.builder().withStageTime(10).withTargetTemp(120).withHeat(HeatType.THERMO_CIRCULATION).build();
        ProgramStage stage5=ProgramStage.builder().withStageTime(10).withTargetTemp(160).withHeat(HeatType.HEATER).build();
        ProgramStage stage6=ProgramStage.builder().withStageTime(10).withTargetTemp(180).withHeat(HeatType.GRILL).build();
        ProgramStage stage7=ProgramStage.builder().withStageTime(10).withTargetTemp(120).withHeat(HeatType.THERMO_CIRCULATION).build();

        List<ProgramStage> list=List.of(stage1,stage2,stage3,stage4,stage5,stage6,stage7);

        BakingProgram bakingProgram=BakingProgram.builder()
                .withInitialTemp(100)
                .withStages(list).build();

        when(fanMock.isOn()).thenReturn(true);

        oven.start(bakingProgram);

        verify(heatingModuleMock,times(2)).heater(HeatingSettings.builder().withTimeInMinutes(10).withTargetTemp(160).build());
        verify(heatingModuleMock,times(2)).grill(HeatingSettings.builder().withTimeInMinutes(10).withTargetTemp(180).build());
        verify(heatingModuleMock,times(3)).termalCircuit(HeatingSettings.builder().withTimeInMinutes(10).withTargetTemp(120).build());

        verify(fanMock,times(7)).off();
        verify(fanMock,times(3)).on();
    }





}
