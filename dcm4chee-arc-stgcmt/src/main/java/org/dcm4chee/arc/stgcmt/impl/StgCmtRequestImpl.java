/*
 * *** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * J4Care.
 * Portions created by the Initial Developer are Copyright (C) 2016
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * *** END LICENSE BLOCK *****
 */

package org.dcm4chee.arc.stgcmt.impl;

import com.querydsl.core.BooleanBuilder;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4chee.arc.entity.QInstance;
import org.dcm4chee.arc.entity.QSeries;
import org.dcm4chee.arc.entity.QStudy;
import org.dcm4chee.arc.stgcmt.StgCmtRequest;

import javax.enterprise.context.ApplicationScoped;


/**
 * @author Vrinda Nayak <vrinda.nayak@j4care.com>
 * @since Sep 2016
 */
public class StgCmtRequestImpl implements StgCmtRequest {

    private String studyUID;
    private String seriesUID;
    private String sopUID;
    private Sequence refSopSeq;

    public StgCmtRequestImpl(String studyUID, String seriesUID, String sopUID, Sequence refSopSeq) {
        this.studyUID = studyUID;
        this.seriesUID = seriesUID;
        this.sopUID = sopUID;
        this.refSopSeq = refSopSeq;
    }

    @Override
    public Sequence getRefSopSequence() {
        return refSopSeq;
    }

    @Override
    public String getStudyUID() {
        return studyUID;
    }

    @Override
    public String getSeriesUID() {
        return seriesUID;
    }

    @Override
    public String getSOPUID() {
        return sopUID;
    }

    @Override
    public BooleanBuilder createPredicate() {
        if (refSopSeq != null)
            return createPredicate(refSopSeq);
        BooleanBuilder builder = new BooleanBuilder();
        if (studyUID != null)
            builder.and(QStudy.study.studyInstanceUID.eq(studyUID));
        if (seriesUID != null)
            builder.and(QSeries.series.seriesInstanceUID.eq(seriesUID));
        if (sopUID != null)
            builder.and(QInstance.instance.sopInstanceUID.eq(sopUID));
        return builder;
    }

    private BooleanBuilder createPredicate(Sequence refSopSeq) {
        BooleanBuilder builder = new BooleanBuilder();
        int size = refSopSeq.size();
        String[] sopIUIDs = new String[size];
        for (int i = 0; i < size; i++)
            sopIUIDs[i] = refSopSeq.get(i).getString(Tag.ReferencedSOPInstanceUID);
        builder.and(QInstance.instance.sopInstanceUID.in(sopIUIDs));
        return builder;
    }
}