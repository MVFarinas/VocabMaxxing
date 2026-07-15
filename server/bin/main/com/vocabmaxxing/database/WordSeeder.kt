package com.vocabmaxxing.database

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

data class SeedWord(
    val word: String,
    val definition: String,
    val exampleSentence: String,
    val tier: String,
    val rarityScore: Int
)

object WordSeeder {

    private val seedWords = listOf(
        // === ACADEMIC (30) ===
        SeedWord("ubiquitous", "Present, appearing, or found everywhere.", "Mobile devices have become ubiquitous in modern society.", "Academic", 5),
        SeedWord("paradigm", "A typical example or pattern of something; a model.", "The discovery fundamentally shifted the scientific paradigm.", "Academic", 4),
        SeedWord("empirical", "Based on observation or experience rather than theory.", "The study relies on empirical evidence gathered over a decade.", "Academic", 5),
        SeedWord("dichotomy", "A division or contrast between two things.", "The dichotomy between theory and practice remains unresolved.", "Academic", 6),
        SeedWord("epistemology", "The branch of philosophy concerned with the nature of knowledge.", "Her research in epistemology questions how we validate beliefs.", "Academic", 8),
        SeedWord("heuristic", "A practical approach to problem-solving that is not guaranteed to be optimal.", "Engineers often rely on heuristic methods when exact solutions are intractable.", "Academic", 7),
        SeedWord("ontological", "Relating to the nature of being or existence.", "The philosopher raised ontological questions about digital consciousness.", "Academic", 8),
        SeedWord("axiom", "A statement accepted as true as the basis for argument.", "The proof rests on a single uncontested axiom.", "Academic", 6),
        SeedWord("extrapolate", "To extend known information to estimate unknown values.", "We can extrapolate future trends from the existing dataset.", "Academic", 5),
        SeedWord("conjecture", "An opinion or conclusion formed on incomplete information.", "The mathematician proposed a conjecture that remains unproven.", "Academic", 5),
        SeedWord("nascent", "Just beginning to develop; emerging.", "The nascent technology shows remarkable promise for scalability.", "Academic", 6),
        SeedWord("synthesis", "The combination of ideas into a coherent whole.", "The paper offers a synthesis of competing theoretical frameworks.", "Academic", 4),
        SeedWord("salient", "Most noticeable or important.", "The most salient finding was the correlation between income and health outcomes.", "Academic", 6),
        SeedWord("antithetical", "Directly opposed or contrasted.", "His methods are antithetical to established research protocols.", "Academic", 7),
        SeedWord("corroborate", "To confirm or give support to a statement or theory.", "Independent experiments corroborate the original findings.", "Academic", 6),
        SeedWord("predicate", "To found or base something on.", "The argument is predicated on the assumption of rational actors.", "Academic", 6),
        SeedWord("delineate", "To describe or portray precisely.", "The report delineates the boundaries of acceptable risk.", "Academic", 7),
        SeedWord("nomenclature", "The system of names used in a particular field.", "Medical nomenclature can be opaque to those outside the discipline.", "Academic", 7),
        SeedWord("taxonomy", "A scheme of classification.", "The taxonomy of cognitive biases continues to evolve.", "Academic", 6),
        SeedWord("elucidate", "To make something clear; to explain.", "The lecturer sought to elucidate the complexities of quantum mechanics.", "Academic", 7),
        SeedWord("pedagogy", "The method and practice of teaching.", "Progressive pedagogy emphasizes critical thinking over rote memorization.", "Academic", 7),
        SeedWord("dialectical", "Relating to logical discussion or the resolution of opposing ideas.", "The dialectical process revealed deeper layers of the argument.", "Academic", 8),
        SeedWord("exegesis", "Critical explanation or interpretation of a text.", "The scholar published an exhaustive exegesis of the manuscript.", "Academic", 9),
        SeedWord("prolegomenon", "A critical or discursive introduction to a work.", "The prolegomenon sets the philosophical context for the treatise.", "Academic", 9),
        SeedWord("hermeneutic", "Relating to the interpretation of texts or meaning.", "A hermeneutic approach transforms how we read historical documents.", "Academic", 8),
        SeedWord("inductive", "Characterized by reasoning from specific cases to general principles.", "Inductive reasoning forms the backbone of scientific inquiry.", "Academic", 5),
        SeedWord("reify", "To make something abstract more concrete or real.", "The model attempts to reify abstract economic forces into measurable variables.", "Academic", 8),
        SeedWord("praxis", "Practice, as distinguished from theory.", "True understanding emerges from the intersection of theory and praxis.", "Academic", 7),
        SeedWord("teleological", "Relating to design or purpose in natural phenomena.", "The teleological argument posits that complexity implies intentional design.", "Academic", 9),
        SeedWord("syllogism", "A form of reasoning in which a conclusion is drawn from two propositions.", "The syllogism falls apart when one examines its hidden premises.", "Academic", 7),

        // === ELITE (30) ===
        SeedWord("perspicacious", "Having keen mental perception and understanding.", "Her perspicacious analysis uncovered patterns others had overlooked.", "Elite", 9),
        SeedWord("ineffable", "Too great or extreme to be expressed in words.", "The ineffable beauty of the aurora left the audience silent.", "Elite", 8),
        SeedWord("recondite", "Little known; abstruse.", "His recondite knowledge of medieval alchemy impressed the faculty.", "Elite", 9),
        SeedWord("pellucid", "Translucently clear; easily understood.", "Her pellucid prose made complex philosophy accessible to all.", "Elite", 9),
        SeedWord("verisimilitude", "The appearance of being true or real.", "The novel achieves remarkable verisimilitude through meticulous detail.", "Elite", 8),
        SeedWord("palimpsest", "Something reused or altered but still bearing traces of its earlier form.", "The city is a palimpsest of architectural styles spanning centuries.", "Elite", 9),
        SeedWord("sycophantic", "Behaving in an obsequious way to gain advantage.", "The sycophantic praise from his subordinates masked genuine discontent.", "Elite", 7),
        SeedWord("loquacious", "Tending to talk a great deal.", "The loquacious host monopolized the evening with endless anecdotes.", "Elite", 7),
        SeedWord("lugubrious", "Looking or sounding sad and dismal.", "The lugubrious tone of the speech dampened the celebratory atmosphere.", "Elite", 8),
        SeedWord("supercilious", "Behaving as though one thinks they are superior.", "His supercilious demeanor alienated even his closest allies.", "Elite", 7),
        SeedWord("magnanimous", "Generous or forgiving, especially toward a rival.", "The magnanimous gesture of the victor earned widespread admiration.", "Elite", 6),
        SeedWord("penumbra", "The partially shaded region or a surrounding area of uncertainty.", "The ruling exists in the penumbra between settled law and judicial interpretation.", "Elite", 8),
        SeedWord("apotheosis", "The highest point of development; a perfect example.", "The symphony represented the apotheosis of the Romantic tradition.", "Elite", 8),
        SeedWord("insouciant", "Showing a casual lack of concern.", "His insouciant attitude toward deadlines frustrated the entire team.", "Elite", 8),
        SeedWord("ebullient", "Cheerful and full of energy.", "The ebullient crowd celebrated the unexpected breakthrough.", "Elite", 7),
        SeedWord("obfuscate", "To render obscure or unclear.", "The report was designed to obfuscate the true financial position.", "Elite", 7),
        SeedWord("compendious", "Containing or presenting the essential facts in a comprehensive but concise way.", "The compendious guide distilled years of research into actionable insight.", "Elite", 9),
        SeedWord("raffish", "Unconventional and slightly disreputable, in a charming way.", "His raffish charm made him a fixture at every literary gathering.", "Elite", 9),
        SeedWord("heterodox", "Not conforming with accepted standards or beliefs.", "Her heterodox views on monetary policy provoked fierce debate.", "Elite", 8),
        SeedWord("liminal", "Occupying a position at, or on both sides of, a boundary.", "The liminal space between waking and sleep generates vivid imagery.", "Elite", 8),
        SeedWord("sesquipedalian", "Given to using long words.", "His sesquipedalian tendencies sometimes obscured otherwise valid arguments.", "Elite", 10),
        SeedWord("propinquity", "Nearness in place, time, or relationship.", "The propinquity of their offices fostered an unexpected collaboration.", "Elite", 9),
        SeedWord("solecism", "A grammatical mistake or a breach of etiquette.", "The diplomatic solecism strained relations between the delegations.", "Elite", 9),
        SeedWord("demotic", "Relating to ordinary people; colloquial.", "The politician adopted a demotic register to connect with rural voters.", "Elite", 8),
        SeedWord("antediluvian", "Ridiculously old-fashioned.", "Their antediluvian IT infrastructure had become a liability.", "Elite", 8),
        SeedWord("anodyne", "Not likely to provoke dissent; blandly inoffensive.", "The CEO issued an anodyne statement that satisfied no one.", "Elite", 8),
        SeedWord("etiolate", "To make or become pale and weak.", "Years of bureaucracy had etiolated the organization's original vision.", "Elite", 9),
        SeedWord("ineluctable", "Unable to be resisted or avoided.", "The ineluctable march of automation continues to reshape labor markets.", "Elite", 9),
        SeedWord("coruscating", "Sparkling or gleaming; brilliantly witty.", "The essayist delivered a coruscating critique of contemporary politics.", "Elite", 9),
        SeedWord("pleonasm", "The use of more words than necessary to convey meaning.", "Editing for pleonasm sharpened the manuscript considerably.", "Elite", 9),

        // === PROFESSIONAL (30) ===
        SeedWord("leverage", "To use something to maximum advantage.", "The firm plans to leverage its proprietary data for competitive advantage.", "Professional", 3),
        SeedWord("scalable", "Able to be changed in size or scale.", "The architecture must be scalable to accommodate exponential user growth.", "Professional", 4),
        SeedWord("synergy", "The interaction of elements that produces a combined effect greater than the sum of individual effects.", "The merger created synergy between the engineering and marketing divisions.", "Professional", 4),
        SeedWord("attrition", "Gradual reduction in strength or numbers.", "Employee attrition has increased since the restructuring was announced.", "Professional", 5),
        SeedWord("bandwidth", "The capacity to deal with information or tasks.", "The team lacks the bandwidth to take on additional projects this quarter.", "Professional", 3),
        SeedWord("deliverable", "A tangible or intangible output produced as a result of a project.", "Each sprint concludes with a clearly defined deliverable.", "Professional", 3),
        SeedWord("streamline", "To make more efficient by employing faster or simpler methods.", "Automation will streamline the approval workflow significantly.", "Professional", 4),
        SeedWord("stakeholder", "A person with an interest or concern in a business or enterprise.", "Key stakeholders must be consulted before finalizing the roadmap.", "Professional", 3),
        SeedWord("due diligence", "Comprehensive appraisal undertaken before a transaction.", "Due diligence revealed discrepancies in the reported revenue figures.", "Professional", 4),
        SeedWord("pivot", "To fundamentally change the direction of a business strategy.", "The startup decided to pivot after market validation showed declining demand.", "Professional", 4),
        SeedWord("vertical", "A specific industry or market niche.", "Their SaaS product dominates the healthcare vertical.", "Professional", 4),
        SeedWord("granular", "Detailed; composed of small, distinguishable pieces.", "The dashboard provides granular insight into customer behavior.", "Professional", 5),
        SeedWord("arbitrage", "Exploiting price differences across markets for profit.", "The fund specializes in statistical arbitrage across equity pairs.", "Professional", 6),
        SeedWord("depreciation", "The reduction in value of an asset over time.", "Accelerated depreciation provides a significant tax advantage.", "Professional", 5),
        SeedWord("fiduciary", "Involving trust, especially regarding the management of money.", "Board members have a fiduciary duty to act in shareholders' interests.", "Professional", 6),
        SeedWord("amortize", "To gradually write off the initial cost of an asset.", "The capital expenditure will be amortized over five fiscal years.", "Professional", 6),
        SeedWord("liquidity", "The availability of assets that can be quickly converted to cash.", "The firm's liquidity position deteriorated during the credit squeeze.", "Professional", 5),
        SeedWord("solvency", "The ability to meet long-term financial obligations.", "Regulatory stress tests are designed to verify institutional solvency.", "Professional", 6),
        SeedWord("paradigm shift", "A fundamental change in approach or underlying assumptions.", "Remote work represents a paradigm shift in organizational design.", "Professional", 5),
        SeedWord("headwinds", "Forces that impede business progress.", "Regulatory headwinds slowed the product's international expansion.", "Professional", 5),
        SeedWord("tailwinds", "Favorable conditions that accelerate business progress.", "The AI boom provided tailwinds for semiconductor companies.", "Professional", 5),
        SeedWord("runway", "The amount of time a company can operate before running out of funds.", "With current burn rate, the startup has eighteen months of runway.", "Professional", 4),
        SeedWord("moat", "A competitive advantage that protects a company from rivals.", "Network effects create a durable moat around the platform.", "Professional", 5),
        SeedWord("accretive", "Contributing to growth or increase.", "The acquisition is expected to be accretive to earnings within two quarters.", "Professional", 7),
        SeedWord("disintermediation", "The removal of intermediaries in a supply chain.", "Blockchain enables disintermediation of traditional financial gatekeepers.", "Professional", 7),
        SeedWord("externality", "A side effect or consequence of activity that affects other parties.", "Carbon emissions represent a negative externality not priced into production costs.", "Professional", 6),
        SeedWord("fungible", "Mutually interchangeable; replaceable by another identical item.", "Commodity inputs are fungible, making price the primary differentiator.", "Professional", 7),
        SeedWord("idiosyncratic", "Peculiar to the individual; unique risk not correlated with the market.", "Idiosyncratic risk can be diversified away through portfolio construction.", "Professional", 7),
        SeedWord("secular", "Relating to long-term trends not tied to cyclical patterns.", "The secular decline in interest rates reshaped fixed-income investing.", "Professional", 6),
        SeedWord("asymmetric", "Having unequal risk and reward profiles.", "The trade offered an asymmetric payoff: limited downside with substantial upside.", "Professional", 6),
    )

    fun seed() {
        transaction {
            val existingCount = Words.selectAll().count()
            if (existingCount > 0) {
                println("Words table already seeded ($existingCount words). Skipping.")
                return@transaction
            }

            seedWords.forEach { sw ->
                Words.insert {
                    it[id] = UUID.randomUUID().toString()
                    it[word] = sw.word
                    it[definition] = sw.definition
                    it[exampleSentence] = sw.exampleSentence
                    it[tier] = sw.tier
                    it[rarityScore] = sw.rarityScore
                }
            }
            println("Seeded ${seedWords.size} words.")
        }
    }
}
