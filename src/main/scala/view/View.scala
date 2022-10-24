package view
import model.entities.Entity
import model.common.Utilities.MetaData

object View {
  trait EntityRenderer:
    def renderEntities(entities: List[Entity]): Unit

    /**
     *
     * @param metaData
     */
    def renderMetadata(metaData: MetaData): Unit
}

