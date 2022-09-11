package fortunate.signature.penit.xml

import fortunate.signature.penit.model.BaseNote

class Backup(
    val baseNotes: List<BaseNote>,
    val deletedNotes: List<BaseNote>,
    val archivedNotes: List<BaseNote>,
    val labels: HashSet<String>
)