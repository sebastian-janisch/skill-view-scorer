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
import java.util.Objects;

import org.sjanisch.skillview.analysis.api.ContributionScore;
import org.sjanisch.skillview.analysis.api.ContributionScorer;
import org.sjanisch.skillview.analysis.api.ScoreOriginator;
import org.sjanisch.skillview.analysis.impl.SkillTags;
import org.sjanisch.skillview.contribution.api.Contribution;

/**
 * Scores {@code 1} if the {@link Contribution#getPath() contribution path} ends
 * in {@code .java} (case insensitive) and nothing else wise.
 * 
 * @author sebastianjanisch
 *
 */
public class JavaFileNameContributionScorer implements ContributionScorer {

	private static final String JAVA_FILE_SUFFIX = ".java";
	private static final ScoreOriginator ORIGINATOR = ScoreOriginator
			.of(JavaFileNameContributionScorer.class.getName());

	@Override
	public Collection<ContributionScore> score(Contribution contribution) {
		Objects.requireNonNull(contribution, "contribution");

		if (contribution.getPath().trim().toLowerCase().endsWith(JAVA_FILE_SUFFIX)) {
			return Collections.singleton(
					ContributionScore.of(SkillTags.JAVA, 1.0, ORIGINATOR, contribution.getContributionTime()));
		}

		return Collections.emptySet();
	}

}
