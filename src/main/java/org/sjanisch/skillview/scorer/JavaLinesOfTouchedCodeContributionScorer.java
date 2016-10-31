package org.sjanisch.skillview.scorer;

import java.util.Objects;
import java.util.OptionalDouble;

import org.sjanisch.skillview.core.analysis.api.ContributionScorer;
import org.sjanisch.skillview.core.analysis.api.ContributionScorerDefinition;
import org.sjanisch.skillview.core.analysis.api.ScoreOriginator;
import org.sjanisch.skillview.core.analysis.impl.SkillTags;
import org.sjanisch.skillview.core.contribution.api.Contribution;
import org.sjanisch.skillview.core.contribution.api.ContributionItem;
import org.sjanisch.skillview.core.diff.api.ContentDiff;
import org.sjanisch.skillview.core.diff.api.ContentDiffService;

/**
 * Implements a contribution scorer against the number of lines of code a
 * contributor worked on.
 * <p>
 * Lines of code are counted and added up but the final score is square rooted
 * based on the notion that skill does not increase linearly with more lines of
 * code produced.
 * 
 * @author sebastianjanisch
 *
 */
public class JavaLinesOfTouchedCodeContributionScorer implements ContributionScorer {

	private static final String JAVA_FILE_SUFFIX = ".java";

	private final ContentDiffService diffService;

	/**
	 * 
	 * @param diffService
	 *            must not be {@code null}
	 */
	public JavaLinesOfTouchedCodeContributionScorer(ContentDiffService diffService) {
		this.diffService = Objects.requireNonNull(diffService, "diffService");
	}

	@Override
	public OptionalDouble score(Contribution contribution) {
		Objects.requireNonNull(contribution, "contribution");

		double score = 0.0;
		for (ContributionItem item : contribution.getContributionItems()) {
			if (item.getPath().toLowerCase().endsWith(JAVA_FILE_SUFFIX)) {
				String previousContent = item.getPreviousContent();
				String content = item.getContent();
				ContentDiff diff = diffService.diff(previousContent, content);
				double touchedLines = diff.getTouchedContent().stream().mapToDouble(String::length).sum();
				score += touchedLines;
			}
		}

		double finalScore = Math.sqrt(score);

		return OptionalDouble.of(finalScore);
	}

	@Override
	public ContributionScorerDefinition getDefinition() {
		return ContributionScorerDefinition.of(ScoreOriginator.of(getClass().getName()), SkillTags.JAVA, 0.0);
	}

}
