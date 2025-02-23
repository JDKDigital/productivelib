package cy.jdkdigital.productivelib.common.block.entity;

import cy.jdkdigital.productivelib.util.MultiBlockDetector;

public interface IMultiBlockControllerBlockEntity
{
    void setMultiBlockData(MultiBlockDetector.MultiBlockData multiBlockData);

    MultiBlockDetector.MultiBlockData getMultiblockData();
}
