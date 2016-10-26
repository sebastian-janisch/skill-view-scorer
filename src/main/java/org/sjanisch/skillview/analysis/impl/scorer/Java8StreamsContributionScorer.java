/*
MIT License

Copyright (c) 2016 Sebastian Janisch

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.sjanisch.skillview.analysis.impl.scorer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sjanisch.skillview.analysis.api.ContributionScore;
import org.sjanisch.skillview.analysis.api.ContributionScorer;
import org.sjanisch.skillview.analysis.api.ScoreOriginator;
import org.sjanisch.skillview.analysis.impl.SkillTags;
import org.sjanisch.skillview.contribution.api.Contribution;
import org.sjanisch.skillview.diff.api.ContentDiff;
import org.sjanisch.skillview.diff.api.ContentDiffService;

/**
 * Scores if the contribution contains usage of Java 8 Streams.
 * <p>
 * This implementation is thread-safe.
 * 
 * @author sebastianjanisch
 *
 */
public class Java8StreamsContributionScorer implements ContributionScorer {

	private static final String JAVA_SUFFIX = ".java";
	private static final ScoreOriginator ORIGINATOR = ScoreOriginator.of(Java8StreamsContributionScorer.class.getName());

	private final ContentDiffService diffService;

	/**
	 * 
	 * @param diffService
	 *            must not be {@code null}
	 */
	public Java8StreamsContributionScorer(ContentDiffService diffService) {
		this.diffService = Objects.requireNonNull(diffService, "diffService");
	}

	@Override
	public Collection<ContributionScore> score(Contribution contribution) {
		Objects.requireNonNull(contribution, "contribution");

		if (!contribution.getPath().toLowerCase().trim().endsWith(JAVA_SUFFIX)) {
			return Collections.emptySet();
		}

		ContentDiff diff = diffService.diff(contribution.getPreviousContent(), contribution.getContent());

		// @formatter:off
		List<ContributionScore> result = diff
				.getTouchedContent()
				.stream()
				.map(toContributionScore())
				.filter(score -> score != null)
				.collect(Collectors.toList());
		// @formatter:on

		return result;
	}

	private Function<String, ContributionScore> toContributionScore() {
		return content -> {
			double score = 0.0;
			if (content.contains(".stream()")) {
				++score;
			}
			if (content.contains(".parallelStream()")) {
				++score;
			}
			return score > 0 ? ContributionScore.of(SkillTags.JAVA8_STREAMS, score, ORIGINATOR) : null;
		};
	}

}
